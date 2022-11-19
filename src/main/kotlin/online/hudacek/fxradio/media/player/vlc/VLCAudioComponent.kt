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

package online.hudacek.fxradio.media.player.vlc

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.value
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.log.LogEventListener
import uk.co.caprica.vlcj.log.LogLevel
import uk.co.caprica.vlcj.log.NativeLog
import uk.co.caprica.vlcj.media.MediaEventListener
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

private val logger = KotlinLogging.logger {}

/**
 * VLC Player Audio Component
 * Implements playing logic using VLC player
 */
class VLCAudioComponent {

    /**
     * Init VLC player with no-video argument since we do not have video player view in the app
     */
    private val playerArgs = listOf(
        "--quiet",
        "--intf=dummy",
        "--no-video"
    )

    private var volume = Properties.Volume.value(30.0)

    private val player = AudioPlayerComponent(MediaPlayerFactory(playerArgs))

    private val mainScope = MainScope()

    private lateinit var nativeLog: NativeLog

    fun play(streamUrl: String) {
        player.mediaPlayer().media().prepare(streamUrl)
        player.mediaPlayer().media().play(streamUrl)

        // Workaround for a very strange VLC bug...
        mainScope.launch {
            delay(800L)
            setVolume(volume)
        }
    }

    fun setVolume(newVolume: Double) {
        logger.debug { "Setting volume to $newVolume" }
        player.mediaPlayer().audio().setVolume(newVolume.toInt())
        volume = newVolume
    }

    fun cancel() {
        runCatching {
            player.let {
                // It is not allowed to call back into LibVLC from an event handling thread,
                // so submit() is used
                it.mediaPlayer().submit {
                    it.mediaPlayer().controls().stop()
                }
            }
        }.onFailure { logger.error(it) { "Can't cancel playing..." } }
    }

    fun attachMediaListener(listener: MediaEventListener) =
        player.mediaPlayer().events().addMediaEventListener(listener)

    fun removeMediaListener(listener: MediaEventListener) =
        player.mediaPlayer().events().removeMediaEventListener(listener)

    fun attachLogListener(listener: LogEventListener, level: LogLevel = LogLevel.NOTICE) = player.let {
        nativeLog = it.mediaPlayerFactory().application().newLog().apply {
            this.level = level
            addLogListener(listener)
        }
    }

    fun releaseLogListener(listener: LogEventListener) = player.let {
        nativeLog.removeLogListener(listener)
        nativeLog.release()
    }

    fun release() = player.release()
}
