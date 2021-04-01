
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

import mu.KotlinLogging
import online.hudacek.fxradio.media.player.humble.HumblePlayerImpl
import online.hudacek.fxradio.media.player.vlc.VLCPlayerImpl

private val logger = KotlinLogging.logger {}

object MediaPlayerFactory {

    /**
     * Create MediaPlayer from String [playerType] identifier
     */
    fun create(playerType: String): MediaPlayer {
        logger.info { "MediaPlayer $playerType initializing" }
        return when (playerType.asPlayerType()) {
            MediaPlayer.Type.VLC -> tryLoadVLCPlayer()
            MediaPlayer.Type.Humble -> HumblePlayerImpl()
        }
    }

    /**
     * Create opposite playerType than provided in [playerType] var
     */
    fun toggle(playerType: MediaPlayer.Type): MediaPlayer {
        logger.info { "MediaPlayer $playerType toggling" }
        return when (playerType) {
            MediaPlayer.Type.Humble -> tryLoadVLCPlayer()
            MediaPlayer.Type.VLC -> HumblePlayerImpl()
        }
    }

    /**
     * Tries to load VLCPlayer. If it is not installed on the system,
     * it loads the Humble player instead.
     */
    private fun tryLoadVLCPlayer(): MediaPlayer = try {
        VLCPlayerImpl()
    } catch (e: Exception) {
        logger.error(e) { "VLC can't be initialized." }
        HumblePlayerImpl()
    }

    /**
     * Helper for loading of playerType from app.properties file
     */
    private fun String.asPlayerType() = try {
        MediaPlayer.Type.valueOf(this)
    } catch (e: IllegalArgumentException) {
        logger.error(e) { "This playerType is invalid. Using Humble MediaPlayer as fallback" }
        MediaPlayer.Type.Humble
    }
}