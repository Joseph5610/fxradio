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

package online.hudacek.broadcastsfx.media

import mu.KotlinLogging
import online.hudacek.broadcastsfx.events.MediaMeta
import uk.co.caprica.vlcj.log.LogEventListener
import uk.co.caprica.vlcj.log.LogLevel
import uk.co.caprica.vlcj.log.NativeLog
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.Meta
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

internal class VLCMediaPlayer(private val mediaPlayer: MediaPlayerWrapper)
    : MediaPlayer {

    private val logger = KotlinLogging.logger {}

    private val audioPlayerComponent: AudioPlayerComponent? by lazy {
        try {
            AudioPlayerComponent()
        } catch (e: UnsatisfiedLinkError) {
            null
        }
    }

    //VLC Logs
    private lateinit var nativeLog: NativeLog
    private val nativeLogListener = LogEventListener { level, module, file, line, name, header, id, message ->
        lastLogMessage = message
        logger.debug { String.format("[%s] (%s) %7s: %s\n", module, name, level, message) }
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
                        && it[Meta.TITLE] != null
                        && it[Meta.GENRE] != null) {
                    mediaPlayer.mediaMetaChanged(
                            MediaMeta(it[Meta.TITLE],
                                    it[Meta.GENRE],
                                    it[Meta.NOW_PLAYING]
                                            .replace("\r", "")
                                            .replace("\n", "")))
                }
            }
        }
    }

    init {
        if (audioPlayerComponent == null) {
            throw RuntimeException("VLClib cannot be found on the system.")
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

    override fun play(url: String) {
        audioPlayerComponent?.let {
            it.mediaPlayer().media().play(url)
        }
    }

    override fun changeVolume(volume: Double): Boolean {
        val vlcVolume: Int =
                if (volume < -29.5) {
                    0
                } else {
                    ((volume + 50) * (100 / 95)).toInt()
                }

        return audioPlayerComponent!!.mediaPlayer().audio().setVolume(vlcVolume)
    }

    /**
     * Called on libVLC event thread
     */
    private fun end(result: Int) {
        logger.debug { "ending current stream if any with error status $result" }

        if (result == 1) {
            mediaPlayer.handleError(RuntimeException("$lastLogMessage\nSee app.log for more details."))
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
            logger.debug { "stop failed, probably already stopped" }
        }
    }

    override fun cancelPlaying() {
        audioPlayerComponent?.mediaPlayer()?.controls()?.stop()
    }

    override fun releasePlayer() {
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