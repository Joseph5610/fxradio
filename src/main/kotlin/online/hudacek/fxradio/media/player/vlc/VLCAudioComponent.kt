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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.log.LogLevel
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

private val logger = KotlinLogging.logger {}

/**
 * VLC Player Audio Component
 * Implements playing logic using VLC player
 */
class VLCAudioComponent {

    private val mainScope = MainScope()

    private val vlcLogListener = VLCLogListener()

    private val vlcMediaAdapter = VLCMediaAdapter()

    /**
     * Init VLC player with no-video argument since we do not have video player view in the app
     */
    private val player = AudioPlayerComponent(MediaPlayerFactory(playerArgs)).apply {
        mediaPlayer().events().addMediaEventListener(vlcMediaAdapter)
    }

    private val nativeLog = player.mediaPlayerFactory().application().newLog().apply {
        level = LogLevel.DEBUG
    }.also { it.addLogListener(vlcLogListener) }

    fun play(streamUrl: String) = player.mediaPlayer().media().play(streamUrl)

    fun setVolume(newVolume: Double) = mainScope.launch {
        withContext(Dispatchers.IO) {
            delay(500L)

            // Workaround for a very strange VLC bug probably only on Ventura? ...
            with(player) {
                if (newVolume.toInt() == 0) {
                    do {
                        mediaPlayer().audio().isMute = true
                    } while (mediaPlayer().status().isPlayable && !mediaPlayer().audio().isMute)
                } else {
                    mediaPlayer().audio().isMute = false
                    do {
                        mediaPlayer().audio().setVolume(newVolume.toInt())
                    } while (mediaPlayer().status().isPlayable &&
                        (mediaPlayer().audio().volume() != newVolume.toInt())
                    )
                }
            }
        }
    }

    fun cancel() = runCatching {
        player.let {
            // It is not allowed to call back into LibVLC from an event handling thread,
            // so submit() is used
            it.mediaPlayer().submit {
                it.mediaPlayer().controls().stop()
            }
        }
    }.onFailure { logger.error(it) { "Can't cancel playing..." } }

    fun release() = with(player) {
        mediaPlayer().events().removeMediaEventListener(vlcMediaAdapter)

        // Clean up NativeLog
        with(nativeLog) {
            removeLogListener(vlcLogListener)
            release()
        }

        // Release Player Component
        release()
    }

    companion object {
        private val playerArgs = listOf(
            "--quiet",
            "--intf=dummy",
            "--no-video"
        )
    }
}
