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

package online.hudacek.fxradio.media.player.humble

import io.humble.video.Demuxer
import io.humble.video.MediaPacket
import io.humble.video.javaxsound.AudioFrame
import io.humble.video.javaxsound.MediaAudioConverterFactory
import kotlinx.coroutines.*
import mu.KotlinLogging
import online.hudacek.fxradio.media.StreamUnavailableException
import java.nio.ByteBuffer
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

private val logger = KotlinLogging.logger {}

/**
 * Play/stop logic of ffmpeg player
 */
class HumbleAudioComponent {

    private var lastTriedVolumeChange = 0.0

    private var audioFrame: AudioFrame? = null
    private var job: Job? = null

    fun play(streamUrl: String) {
        job = GlobalScope.launch(coroutineExceptionHandler) {
            val deMuxer = Demuxer.make()
            try {
                val streamInfo = deMuxer.getStreamInfo(streamUrl)
                streamInfo?.decoder?.let {
                    val samples = it.getSamples()
                    val converter = MediaAudioConverterFactory.createConverter(
                            MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO, samples)

                    audioFrame = AudioFrame.make(converter.javaFormat)
                            ?: throw StreamUnavailableException("No output device available!")

                    var rawAudio: ByteBuffer? = null

                    //Log audio format
                    logger.info { audioFrame?.format }

                    changeLineVolume(lastTriedVolumeChange)

                    val packet = MediaPacket.make()
                    while (deMuxer.read(packet) >= 0) {
                        if (!isActive) break
                        if (packet.streamIndex == streamInfo.streamId) {
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
                }
            } finally {
                deMuxer.close()
                audioFrame?.dispose()
            }
        }
    }

    fun changeLineVolume(newVolume: Double) {
        lastTriedVolumeChange = newVolume

        //Dirty hack, since the api does not provide direct access to field,
        //we use reflection to get access to line field to change volume
        AudioFrame::class.memberProperties
                .filter { it.name == "mLine" }[0].apply {
            isAccessible = true
            val lineValue = getter.call(audioFrame) as SourceDataLine
            val gainControl = lineValue.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            gainControl.value = newVolume.toFloat()
        }
    }

    fun cancel() {
        job?.let {
            if (it.isActive) {
                logger.debug { "Cancelling playback" }
                it.cancel()
            }
        }
    }

    companion object {
        /**
         * Handler for unexpected exception inside [job]
         */
        private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logger.error(throwable) { "Stream unavailable..." }
        }
    }
}

