package online.hudacek.broadcastsfx.extension

import io.humble.video.*
import io.humble.video.javaxsound.AudioFrame
import io.humble.video.javaxsound.MediaAudioConverterFactory
import javafx.application.Platform
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.nio.ByteBuffer
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.SourceDataLine
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

object MediaPlayerWrapper {

    private val logger = KotlinLogging.logger {}

    private val handler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Platform.runLater {
            tornadofx.error("Can't open stream", exception.localizedMessage)
        }
    }

    var audioFrame: AudioFrame? = null
    var isPlaying: Boolean = false
    var mediaPlayerCoroutine: Job? = null

    fun play(url: String) {
        cancelPlaying()
        mediaPlayerCoroutine = GlobalScope.launch(handler) {

            val demuxer = Demuxer.make()
            val numStreams = demuxer.stream(url)

            var audioStreamId = -1
            var audioDecoder: Decoder? = null
            for (i in 0 until numStreams) {
                val decoder = demuxer.getStream(i).decoder
                if (decoder != null && decoder.codecType == MediaDescriptor.Type.MEDIA_AUDIO) {
                    audioStreamId = i
                    audioDecoder = decoder
                    break
                }
            }
            if (audioStreamId == -1) throw RuntimeException("could not find audio stream in container")

            audioDecoder!!.open()
            logger.debug { "opened stream" }
            val samples = MediaAudio.make(
                    audioDecoder.frameSize,
                    audioDecoder.sampleRate,
                    audioDecoder.channels,
                    audioDecoder.channelLayout,
                    audioDecoder.sampleFormat)

            val converter = MediaAudioConverterFactory.createConverter(
                    MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO,
                    samples)

            audioFrame = AudioFrame.make(converter.javaFormat) ?: throw LineUnavailableException()

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
                            audioFrame?.play(rawAudio)
                            isPlaying = true
                        }
                        offset += bytesRead
                    } while (offset < packet.size)
                }
            }

            do {
                audioDecoder.decode(samples, null, 0)
                if (samples.isComplete) {
                    rawAudio = converter.toJavaAudio(rawAudio, samples)
                    audioFrame?.play(rawAudio)
                }
            } while (samples.isComplete)
            isPlaying = false

            demuxer.close()
            audioFrame?.dispose()
        }
    }

    fun cancelPlaying() {
        mediaPlayerCoroutine?.cancel()
        isPlaying = false
    }

    fun changeVolume(volume: Float): Boolean {
        return try {
            val frameCls = AudioFrame::class
            val mLine = frameCls.memberProperties.filter { it.name == "mLine" }[0]
            mLine.isAccessible = true
            val lineValue = mLine.getter.call(audioFrame) as SourceDataLine
            val gainControl = lineValue.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            gainControl.value = volume
            true
        } catch (e: Exception) {
            false
        }
    }
}

private fun Decoder.open() = this.open(null, null)

private fun Demuxer.stream(streamUrl: String): Int {
    this.open(streamUrl, null, false, true, null, null)
    return this.numStreams
}