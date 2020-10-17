/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.media.players

import io.humble.video.*
import io.humble.video.javaxsound.AudioFrame
import io.humble.video.javaxsound.MediaAudioConverterFactory
import kotlinx.coroutines.*
import mu.KotlinLogging
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.StreamUnavailableException
import tornadofx.*
import java.nio.ByteBuffer
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineUnavailableException

//Custom Audio player using ffmpeg lib
internal class CustomPlayer : Component(), MediaPlayer {

    private var mediaPlayerCoroutine: Job? = null
    private var audioFrame: AudioFrame? = null

    private var lastTriedVolumeChange = 0.0

    private val logger = KotlinLogging.logger {}

    private val handler = CoroutineExceptionHandler { _, exception ->
        logger.error(exception) { "Unhandled exception." }
    }

    override fun play(streamUrl: String) {
        stop() //this player should stop itself before playing new stream
        mediaPlayerCoroutine = GlobalScope.launch(handler) {
            val demuxer = Demuxer.make()
            try {
                val numStreams = demuxer.stream(streamUrl)

                var audioStreamId = -1
                var audioDecoder: Decoder? = null

                for (i in 0 until numStreams) {
                    val demuxerStream = demuxer.getStream(i)

                    val decoder = demuxerStream.decoder
                    if (decoder != null && decoder.codecType == MediaDescriptor.Type.MEDIA_AUDIO) {
                        audioStreamId = i
                        audioDecoder = decoder
                        break
                    }
                }
                if (audioStreamId == -1) throw RuntimeException("could not find audio stream in container")
                if (audioDecoder == null) throw RuntimeException("could not find audio decoder")

                audioDecoder.open().let {
                    val samples = MediaAudio.make(
                            it.frameSize,
                            it.sampleRate,
                            it.channels,
                            it.channelLayout,
                            it.sampleFormat)

                    //Info about decoder
                    logger.debug { it }

                    val converter = MediaAudioConverterFactory.createConverter(
                            MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO,
                            samples)
                    audioFrame = AudioFrame.make(converter.javaFormat) ?: throw LineUnavailableException()

                    changeVolume(lastTriedVolumeChange)

                    var rawAudio: ByteBuffer? = null

                    //Log audio format
                    logger.info { audioFrame?.format }

                    val packet = MediaPacket.make()
                    while (demuxer.read(packet) >= 0) {
                        if (!isActive) break
                        if (packet.streamIndex == audioStreamId) {
                            var offset = 0
                            var bytesRead = 0
                            do {
                                bytesRead += it.decode(samples, packet, offset)
                                if (samples.isComplete) {
                                    rawAudio = converter.toJavaAudio(rawAudio, samples)
                                    audioFrame?.play(rawAudio)
                                }
                                offset += bytesRead
                            } while (offset < packet.size)
                        }
                    }

                    do {
                        it.decode(samples, null, 0)
                        if (samples.isComplete) {
                            rawAudio = converter.toJavaAudio(rawAudio, samples)
                            audioFrame?.play(rawAudio)
                        }
                    } while (samples.isComplete)
                    demuxer.close()
                }
            } catch (e: Exception) {
                throw StreamUnavailableException(e.localizedMessage, e)
            } finally {
                audioFrame?.dispose()
            }
        }
    }

    override fun changeVolume(newVolume: Double): Boolean {
        return try {
            lastTriedVolumeChange = newVolume

            audioFrame?.let {
                val mixer = AudioSystem.getMixer(null)
                val line = mixer.sourceLines.firstOrNull { line -> line.isOpen }
                line?.let {
                    val gainControl = it.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                    gainControl.value = newVolume.toFloat()
                    true
                }
                false
            }
            false
        } catch (e: Exception) {
            logger.error(e) { "Can't change volume to : $newVolume, will try later" }
            false
        }
    }

    override fun stop() {
        mediaPlayerCoroutine?.isActive.let {
            mediaPlayerCoroutine?.cancel()
        }
    }

    override fun release() = stop()

    private fun Decoder.open() = this.apply {
        open(null, null)
    }

    private fun Demuxer.stream(streamUrl: String?): Int {
        this.open(streamUrl, null, false, true, null, null)
        return numStreams
    }
}