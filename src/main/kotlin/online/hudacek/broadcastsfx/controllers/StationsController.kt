package online.hudacek.broadcastsfx.controllers

import io.humble.video.*
import io.humble.video.javaxsound.AudioFrame
import io.humble.video.javaxsound.MediaAudioConverterFactory
import javafx.application.Platform
import kotlinx.coroutines.*
import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.data.TopStationsModel
import tornadofx.Controller
import java.nio.ByteBuffer
import javax.sound.sampled.LineUnavailableException

object MediaPlayerWrapper {
    var mediaPlayerCoroutine: Job? = null
}

class StationsController : Controller() {

    private var playingStation: TopStationsModel? = null

    val handler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Platform.runLater {
            tornadofx.error("Can't open stream", exception.localizedMessage)
        }
    }

    val stations by lazy {
        StationsApiClient.create()
    }

    fun playStream(station: TopStationsModel) {
        playingStation?.let {
            if (station.stationuuid == it.stationuuid) return
        }

        playingStation = station
        MediaPlayerWrapper.mediaPlayerCoroutine?.cancel()
        println("before launch")
        MediaPlayerWrapper.mediaPlayerCoroutine = GlobalScope.launch(handler) {

            val demuxer = Demuxer.make()
            demuxer.open(station.url_resolved, null, false, true, null, null)
            println("opened stream")

            val numStreams = demuxer.numStreams

            var audioStreamId = -1
            var audioDecoder: Decoder? = null
            for (i in 0 until numStreams) {
                val stream = demuxer.getStream(i)
                val decoder = stream.decoder
                if (decoder != null && decoder.codecType == MediaDescriptor.Type.MEDIA_AUDIO) {
                    audioStreamId = i
                    audioDecoder = decoder
                    break
                }
            }
            if (audioStreamId == -1) throw RuntimeException("could not find audio stream in container")

            audioDecoder!!.open(null, null)

            val samples = MediaAudio.make(
                    audioDecoder.frameSize,
                    audioDecoder.sampleRate,
                    audioDecoder.channels,
                    audioDecoder.channelLayout,
                    audioDecoder.sampleFormat)

            val converter = MediaAudioConverterFactory.createConverter(
                    MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO,
                    samples)

            val audioFrame = AudioFrame.make(converter.javaFormat) ?: throw LineUnavailableException()

            var rawAudio: ByteBuffer? = null

            val packet = MediaPacket.make()
            while (demuxer.read(packet) >= 0) {
                if (!isActive) break
                if (packet.streamIndex == audioStreamId) {
                    var offset = 0
                    var bytesRead = 0
                    do {
                        bytesRead += audioDecoder.decode(samples, packet, offset)
                        if (samples.isComplete) {
                            rawAudio = converter.toJavaAudio(rawAudio, samples)
                            audioFrame.play(rawAudio)
                        }
                        offset += bytesRead
                    } while (offset < packet.size)
                }
            }

            do {
                audioDecoder.decode(samples, null, 0)
                if (samples.isComplete) {
                    rawAudio = converter.toJavaAudio(rawAudio, samples)
                    audioFrame.play(rawAudio)
                }
            } while (samples.isComplete)

            demuxer.close()
            audioFrame.dispose()
        }
    }
}