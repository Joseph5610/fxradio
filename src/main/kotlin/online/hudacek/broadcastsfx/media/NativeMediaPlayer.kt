package online.hudacek.broadcastsfx.media

import io.humble.video.*
import io.humble.video.javaxsound.AudioFrame
import io.humble.video.javaxsound.MediaAudioConverterFactory
import javafx.application.Platform
import kotlinx.coroutines.*
import mu.KotlinLogging
import online.hudacek.broadcastsfx.events.PlayingStatus
import java.nio.ByteBuffer
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.SourceDataLine
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal class NativeMediaPlayer(private val mediaPlayer: MediaPlayerWrapper)
    : MediaPlayer {

    private val logger = KotlinLogging.logger {}

    override var volume: Double = 0.0
    override var playingStatus: PlayingStatus = PlayingStatus.Stopped

    private var mediaPlayerCoroutine: Job? = null
    private var audioFrame: AudioFrame? = null

    init {
        logger.debug { "Native player started" }
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        Platform.runLater {
            mediaPlayer.handleError(exception)
        }
    }

    override fun play(url: String?) {
        mediaPlayerCoroutine = GlobalScope.launch(handler) {
            val demuxer = Demuxer.make()
            try {
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

                with(audioDecoder!!) {
                    audioDecoder.open()
                    val samples = MediaAudio.make(
                            this.frameSize,
                            this.sampleRate,
                            this.channels,
                            this.channelLayout,
                            this.sampleFormat)

                    val converter = MediaAudioConverterFactory.createConverter(
                            MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO,
                            samples)
                    audioFrame = AudioFrame.make(converter.javaFormat) ?: throw LineUnavailableException()
                    logger.debug { "Stream started" }
                    changeVolume(volume)
                    var rawAudio: ByteBuffer? = null

                    val packet = MediaPacket.make()
                    while (demuxer.read(packet) >= 0) {
                        if (!isActive) break
                        if (packet.streamIndex == audioStreamId) {
                            var offset = 0
                            var bytesRead = 0
                            do {
                                bytesRead += decode(samples, packet, offset)
                                if (samples.isComplete) {
                                    rawAudio = converter.toJavaAudio(rawAudio, samples)
                                    audioFrame?.play(rawAudio)
                                    playingStatus = PlayingStatus.Playing
                                }
                                offset += bytesRead
                            } while (offset < packet.size)
                        }
                    }

                    do {
                        decode(samples, null, 0)
                        if (samples.isComplete) {
                            rawAudio = converter.toJavaAudio(rawAudio, samples)
                            audioFrame?.play(rawAudio)
                        }
                    } while (samples.isComplete)
                }
            } finally {
                playingStatus = PlayingStatus.Stopped
                demuxer?.close()
                audioFrame?.dispose()
            }
        }
    }

    override fun changeVolume(volume: Double): Boolean {
        return try {
            val frameCls = AudioFrame::class
            val mLine = frameCls.memberProperties.filter { it.name == "mLine" }[0]
            mLine.isAccessible = true
            val lineValue = mLine.getter.call(audioFrame) as SourceDataLine
            val gainControl = lineValue.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            gainControl.value = volume.toFloat()
            this.volume = volume
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun cancelPlaying() {
        playingStatus = PlayingStatus.Stopped
        mediaPlayerCoroutine?.isActive.let {
            logger.debug { "cancelling player" }
            mediaPlayerCoroutine?.cancel()
        }
    }

    override fun releasePlayer() = cancelPlaying()

    private fun Decoder.open() = this.open(null, null)

    private fun Demuxer.stream(streamUrl: String?): Int {
        this.open(streamUrl, null, false, true, null, null)
        return this.numStreams
    }
}