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

package online.hudacek.fxradio.media

import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.fxradio.media.player.humble.HumblePlayerImpl
import online.hudacek.fxradio.media.player.vlc.VLCPlayerImpl
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.value
import online.hudacek.fxradio.util.vlcAlert

private val logger = KotlinLogging.logger {}

object MediaPlayerFactory {

    private val defaultPlayerType = MediaPlayer.Type.VLC

    /**
     * Create MediaPlayer
     */
    fun create(): MediaPlayer {
        val player = Properties.Player.value(defaultPlayerType.name)
        logger.debug { "MediaPlayer $player is initializing..." }
        return when (player.asPlayerType()) {
            MediaPlayer.Type.VLC -> tryLoadVLCPlayer()
            MediaPlayer.Type.Humble -> HumblePlayerImpl()
        }
    }

    /**
     * Toggle MediaPlayer
     */
    fun toggle(): MediaPlayer {
        logger.debug { "MediaPlayer toggling..." }
        val currentPlayer = Properties.Player.value(defaultPlayerType.name)
        return when (currentPlayer.asPlayerType()) {
            MediaPlayer.Type.Humble -> tryLoadVLCPlayer()
            MediaPlayer.Type.VLC -> HumblePlayerImpl()
        }
    }

    /**
     * Tries to load VLCPlayer. If it is not installed on the system,
     * it loads the Humble player instead.
     */
    private fun tryLoadVLCPlayer(): MediaPlayer = runCatching {
        VLCPlayerImpl()
    }.onFailure {
        logger.error(it) { "Exception when initializing VLC Player!" }
        Platform.runLater {
            vlcAlert()
        }
    }.getOrDefault(HumblePlayerImpl())

    /**
     * Helper for loading of playerType from app.properties file
     */
    private fun String.asPlayerType() = runCatching {
        MediaPlayer.Type.valueOf(this)
    }.onFailure {
        logger.error(it) { "This playerType is invalid. Using Humble MediaPlayer as fallback!" }
    }.getOrDefault(defaultPlayerType)
}
