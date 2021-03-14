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

enum class PlayerType {
    Humble, VLC
}

/**
 * Common interface for all available players
 */
interface MediaPlayer {

    val playerType: PlayerType

    /**
     * Starts playing stream with URL [streamUrl]
     */
    fun play(streamUrl: String)

    /**
     * Changes playing value to [newVolume]
     * Returns true when change was successful, false otherwise
     */
    fun changeVolume(newVolume: Double): Boolean

    fun stop()

    fun release()

    companion object MediaPlayerFactory {
        private val logger = KotlinLogging.logger {}

        //Create MediaPlayer from String identifier
        fun create(playerType: String): MediaPlayer {
            logger.info { "MediaPlayer $playerType initializing" }
            return when (playerType.asPlayerType()) {
                PlayerType.VLC -> tryLoadVLCPlayer()
                PlayerType.Humble -> HumblePlayerImpl()
            }
        }

        fun toggle(playerType: PlayerType): MediaPlayer {
            logger.info { "MediaPlayer $playerType toggling" }
            return when (playerType) {
                PlayerType.Humble -> tryLoadVLCPlayer()
                PlayerType.VLC -> HumblePlayerImpl()
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
            PlayerType.valueOf(this)
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "This playerType is invalid. Returning PlayerType.Humble" }
            PlayerType.Humble
        }
    }
}