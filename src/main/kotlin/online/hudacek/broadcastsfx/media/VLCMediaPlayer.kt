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
import uk.co.caprica.vlcj.log.LogLevel
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.Meta
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

internal class VLCMediaPlayer(private val mediaPlayer: MediaPlayerWrapper)
    : MediaPlayer {

    private val logger = KotlinLogging.logger {}

    private val mediaPlayerComponent by lazy { AudioPlayerComponent() }

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
                            MediaMeta(it[Meta.TITLE], it[Meta.GENRE], it[Meta.NOW_PLAYING]))
                }
            }
        }
    }

    init {
        logger.debug { "VLC player started" }
        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(mediaPlayerEvent)
        mediaPlayerComponent.mediaPlayer().events().addMediaEventListener(mediaEvent)
    }

    override fun play(url: String) {
        changeVolume(mediaPlayer.volume)

        mediaPlayerComponent.mediaPlayerFactory().application().newLog().apply {
            level = LogLevel.NOTICE
            addLogListener { level, module, file, line, name, header, id, message ->
                logger.debug { String.format("[%-20s] (%-20s) %7s: %s\n", module, name, level, message) }
            }
        }
        mediaPlayerComponent.mediaPlayer().media().play(url)
    }

    override fun changeVolume(volume: Double): Boolean {
        logger.debug { "change volume to $volume" }

        val intVol = if (volume < -29.5) {
            0
        } else {
            ((volume + 50) * (100 / 95)).toInt()
        }
        return mediaPlayerComponent.mediaPlayer().audio().setVolume(intVol)
    }

    private fun end(result: Int) {
        logger.debug { "ending current stream if any with error status $result" }

        if (result == 1) {
            mediaPlayer.handleError(RuntimeException("See app.log for more details."))
        }

        // Its not allowed to call back into LibVLC from an event handling thread,
        // so submit() is used
        try {
            mediaPlayerComponent.mediaPlayer().submit {
                mediaPlayerComponent.mediaPlayer().controls().stop()
            }
        } catch (e: Exception) {
            logger.debug { "stop failed, probably already stopped, whatever" }
        }
    }

    override fun cancelPlaying() = end(0)

    override fun releasePlayer() {
        mediaPlayerComponent.mediaPlayer().events().removeMediaEventListener(mediaEvent)
        mediaPlayerComponent.mediaPlayer().events().removeMediaPlayerEventListener(mediaPlayerEvent)

        logger.debug { "releasing player" }
        mediaPlayerComponent.release()
    }
}