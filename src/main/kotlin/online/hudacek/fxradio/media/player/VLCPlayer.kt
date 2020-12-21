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

import mu.KotlinLogging
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.MetaData
import online.hudacek.fxradio.media.MetaDataChanged
import online.hudacek.fxradio.media.StreamUnavailableException
import tornadofx.Component
import tornadofx.get
import uk.co.caprica.vlcj.log.LogEventListener
import uk.co.caprica.vlcj.log.LogLevel
import uk.co.caprica.vlcj.log.NativeLog
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.Meta
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

internal class VLCPlayer : Component(), MediaPlayer {


    private val audioPlayerComponent: AudioPlayerComponent? by lazy {
        try {
            AudioPlayerComponent()
        } catch (e: UnsatisfiedLinkError) {
            null
        }
    }

    private val logger = KotlinLogging.logger {}

    //VLC Logs
    private lateinit var nativeLog: NativeLog
    private val nativeLogListener = LogEventListener { level, module, _, _, name, _, _, message ->
        lastLogMessage = message
        logger.debug { "[$module] ($name) $level: $message" }
    }

    private var lastLogMessage = ""

    private val mediaPlayerEvent = object : MediaPlayerEventAdapter() {
        override fun finished(mediaPlayer: uk.co.caprica.vlcj.player.base.MediaPlayer?) {
            end(0)
        }

        override fun error(mediaPlayer: uk.co.caprica.vlcj.player.base.MediaPlayer?) {
            end(1)
        }
    }

    private val mediaEvent = object : MediaEventAdapter() {
        override fun mediaMetaChanged(media: Media?, metaType: Meta?) {
            media?.meta()?.let {
                if (it[Meta.NOW_PLAYING] != null
                        && it[Meta.TITLE] != null) {
                    val metaData = MetaData(it[Meta.TITLE],
                            it[Meta.NOW_PLAYING]
                                    .replace("\r", "")
                                    .replace("\n", ""))
                    fire(MetaDataChanged(metaData))
                }
            }
        }
    }

    init {
        if (audioPlayerComponent == null) {
            throw RuntimeException("VLCLib cannot be found on the system.")
        }

        audioPlayerComponent?.let {
            it.mediaPlayer().events().addMediaPlayerEventListener(mediaPlayerEvent)
            it.mediaPlayer().events().addMediaEventListener(mediaEvent)
            nativeLog = it.mediaPlayerFactory().application().newLog().apply {
                level = LogLevel.NOTICE
                addLogListener(nativeLogListener)
            }
        }
    }

    override fun play(streamUrl: String) {
        audioPlayerComponent?.mediaPlayer()?.media()?.play(streamUrl)
    }

    override fun changeVolume(newVolume: Double): Boolean {
        val vlcVolume: Int =
                if (newVolume < -29.5) {
                    0
                } else {
                    ((newVolume + 65) * (100 / 95)).toInt()
                }

        return audioPlayerComponent?.mediaPlayer()?.audio()?.setVolume(vlcVolume) ?: false
    }

    /**
     * Called on libVLC event thread
     */
    private fun end(result: Int) {
        logger.debug { "Ending current stream with status: $result" }

        if (result == 1) {
            val errorMsg = if (lastLogMessage.isEmpty()) messages["player.streamError"] else lastLogMessage
            throw StreamUnavailableException(errorMsg)
        }

        // Its not allowed to call back into LibVLC from an event handling thread,
        // so submit() is used
        try {
            audioPlayerComponent?.let {
                it.mediaPlayer().submit {
                    it.mediaPlayer().controls().stop()
                }
            }
        } catch (e: Exception) {
            logger.debug { "Can't stop stream" }
        }
    }

    override fun stop() {
        audioPlayerComponent?.mediaPlayer()?.let {
            if (it.status().isPlaying) {
                it.controls().stop()
            }
        }
    }

    override fun release() {
        audioPlayerComponent?.let {
            logger.debug { "Releasing events" }

            it.mediaPlayer().events().removeMediaEventListener(mediaEvent)
            it.mediaPlayer().events().removeMediaPlayerEventListener(mediaPlayerEvent)
            nativeLog.removeLogListener(nativeLogListener)

            logger.info { "Releasing player" }
            nativeLog.release()
            it.release()
        }
    }
}