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

import mu.KotlinLogging
import online.hudacek.fxradio.media.MediaPlayer

private val logger = KotlinLogging.logger {}

/**
 * Custom Audio player using VLClib
 * Requires VLC player installed on client system
 */
class VLCPlayerImpl(override val playerType: MediaPlayer.Type = MediaPlayer.Type.VLC) : MediaPlayer {

    private val vlcAudioComponent = VLCAudioComponent()

    override fun play(streamUrl: String) {
        vlcAudioComponent.play(streamUrl)
    }

    override fun changeVolume(newVolume: Double) {
        // Recalculate humble player volume levels to VLC
        val vlcVolume: Double =
            if (newVolume < -34.5) {
                0.0
            } else {
                (newVolume + 65) * 1.1
            }
        logger.trace { "Setting volume to $vlcVolume" }
        vlcAudioComponent.setVolume(vlcVolume)
    }

    override fun stop() {
        vlcAudioComponent.cancel()
    }

    override fun release() {
        logger.info { "Releasing VLC player..." }
        vlcAudioComponent.release()
    }
}
