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

package online.hudacek.fxradio.media.player

import io.humble.video.*
import io.humble.video.javaxsound.AudioFrame
import io.humble.video.javaxsound.MediaAudioConverterFactory
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.util.Duration
import kotlinx.coroutines.*
import mu.KotlinLogging
import online.hudacek.fxradio.Properties
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.MetaData
import online.hudacek.fxradio.media.MetaDataChanged
import online.hudacek.fxradio.media.StreamUnavailableException
import online.hudacek.fxradio.property
import tornadofx.Component
import tornadofx.get
import java.nio.ByteBuffer
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

private val logger = KotlinLogging.logger {}

private data class StreamInfo(val streamId: Int, val decoder: Decoder)

//Custom Audio player using ffmpeg lib
class CustomPlayer : Component(), MediaPlayer {

    private var mediaPlayerCoroutine: Job? = null
    private var audioFrame: AudioFrame? = null

    private var lastTriedVolumeChange = 0.0

    private var streamUrl = ""

    private val playerRefreshMetaProperty = property(Properties.PLAYER_CUSTOM_REFRESH_META, true)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logger.error(throwable) { "Stream unavailable..." }
    }

    override fun play(streamUrl: String) {
        stop() //this player should stop itself before playing new stream

        this.streamUrl = streamUrl

        if (playerRefreshMetaProperty) {
            metaDataService.restart()
        }

        mediaPlayerCoroutine = GlobalScope.launch(coroutineExceptionHandler) {

            val deMuxer = Demuxer.make()
            val streamInfo = deMuxer.getStreamInfo(streamUrl)
            val audioStreamId = streamInfo?.streamId
            val audioDecoder = streamInfo?.decoder

            try {
                audioDecoder?.let {
                    val samples = MediaAudio.make(
                            it.frameSize,
                            it.sampleRate,
                            it.channels,
                            it.channelLayout,
                            it.sampleFormat)

                    //Info about decoder
                    logger.debug { it }

                    val converter = MediaAudioConverterFactory.createConverter(
                            MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO, samples)
                    audioFrame = AudioFrame.make(converter.javaFormat)
                            ?: throw StreamUnavailableException("No output device available!")

                    var rawAudio: ByteBuffer? = null

                    //Log audio format
                    logger.info { audioFrame?.format }

                    changeVolume(lastTriedVolumeChange)

                    val packet = MediaPacket.make()
                    while (deMuxer.read(packet) >= 0) {
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
                }
            } finally {
                deMuxer.close()
                audioFrame?.dispose()
            }
        }
    }

    override fun changeVolume(newVolume: Double): Boolean {
        return try {
            //Dirty hack, since the api does not provide direct access to field,
            //we use reflection to get access to line field to change volume
            lastTriedVolumeChange = newVolume

            AudioFrame::class.memberProperties
                    .filter { it.name == "mLine" }[0].apply {
                isAccessible = true
                val lineValue = getter.call(audioFrame) as SourceDataLine
                val gainControl = lineValue.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                gainControl.value = newVolume.toFloat()
            }
            true
        } catch (e: Exception) {
            logger.debug { "Can't change volume to : $newVolume" }
            false
        }
    }

    override fun stop() {
        if (playerRefreshMetaProperty) {
            metaDataService.cancel()
        }

        mediaPlayerCoroutine?.let {
            if (it.isActive) {
                it.cancel()
            }
        }
    }

    override fun release() = stop()

    /**
     * Returns opened decoder or null when error happened
     */
    private fun Demuxer.getStreamInfo(streamUrl: String): StreamInfo? {
        try {
            open(streamUrl, null, false, true,
                    null, null)
            for (i in 0 until numStreams) {
                val stream = getStream(i)
                val decoder = stream.decoder
                if (decoder != null && decoder.codecType == MediaDescriptor.Type.MEDIA_AUDIO) {
                    decoder.open(null, null)
                    return StreamInfo(i, decoder)
                }
            }
        } catch (e: Exception) {
            throw StreamUnavailableException(messages["player.streamError"])
        }
        return null
    }

    /**
     * Fetch new meta data from playing stream
     */
    private val metaDataService = object : ScheduledService<KeyValueBag>() {
        init {
            period = Duration.seconds(50.0) //period between fetching data
            delay = Duration.seconds(5.0) //initial delay
        }
        override fun createTask(): Task<KeyValueBag> = FetchDataTask()
    }

    inner class FetchDataTask : Task<KeyValueBag>() {
        override fun call(): KeyValueBag {
            val deMuxer = Demuxer.make()
            deMuxer.open(streamUrl, null, false, true, null, null)
            val data = deMuxer.metaData
            deMuxer.close()
            logger.debug { "FetchDataTask: $data" }
            return data
        }

        override fun succeeded() {
            //Get values from demuxer metadata and fire event with the new data
            if (value.getValue("StreamTitle") != null
                    && value.getValue("icy-name") != null) {
                val mediaMeta = MetaData(value.getValue("icy-name"), value.getValue("StreamTitle"))
                fire(MetaDataChanged(mediaMeta))
            }
        }

        override fun failed() = logger.error(exception) { "FetchDataTask failed." }
    }
}