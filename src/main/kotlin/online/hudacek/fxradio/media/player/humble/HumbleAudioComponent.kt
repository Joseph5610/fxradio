/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.media.player.humble

import io.humble.video.Demuxer
import io.humble.video.MediaPacket
import io.humble.video.javaxsound.AudioFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import online.hudacek.fxradio.media.StreamUnavailableException
import java.nio.ByteBuffer
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

private val logger = KotlinLogging.logger {}

/**
 * Play/stop logic of Humble player
 */
class HumbleAudioComponent {

    private var lastTriedVolumeChange = 0.0

    private var audioFrame: AudioFrame? = null
    private var job: Job? = null

    suspend fun play(streamUrl: String) = withContext(Dispatchers.IO) {
        job = launch {
            val deMuxer = Demuxer.make()
            try {
                val streamInfo = deMuxer.getStreamInfo(streamUrl)
                streamInfo?.decoder?.let {
                    val samples = it.getMediaAudio()
                    val converter = HumbleAudioConverter(samples.sampleRate, samples.channelLayout, samples.format)
                    audioFrame = AudioFrame.make(converter.javaFormat)
                        ?: throw StreamUnavailableException("No output device available!")

                    var rawAudio: ByteBuffer? = null

                    setVolume(lastTriedVolumeChange)

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
                    } while (samples.isComplete && isActive)
                }
            } catch (e: Exception) {
                deMuxer.correctlyClose()
            } finally {
                deMuxer.correctlyClose()
                audioFrame?.dispose()
            }
        }
    }

    fun setVolume(newVolume: Double) {
        lastTriedVolumeChange = newVolume

        // Dirty hack, since the api does not provide direct access to field,
        // we use reflection to get access to line field to change volume
        AudioFrame::class.memberProperties
            .filter { it.name == "mLine" }[0].apply {
            isAccessible = true
            val lineValue = getter.call(audioFrame) as SourceDataLine
            val gainControl = lineValue.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            gainControl.value = newVolume.toFloat()
        }
    }

    fun stop() {
        job?.let {
            if (it.isActive) {
                logger.debug { "Cancelling current playback..." }
                it.cancel()
            }
        }
    }
}

