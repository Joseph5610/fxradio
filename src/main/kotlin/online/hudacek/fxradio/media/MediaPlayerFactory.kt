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

package online.hudacek.fxradio.media

import javafx.application.Platform
import javafx.scene.control.Alert
import mu.KotlinLogging
import online.hudacek.fxradio.media.player.humble.HumblePlayerImpl
import online.hudacek.fxradio.media.player.vlc.VLCPlayerImpl
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.value
import tornadofx.alert

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
            alert(Alert.AlertType.WARNING, "VLC player not found!", "For the best experience, we recommend that you install VLC player on your system!")
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