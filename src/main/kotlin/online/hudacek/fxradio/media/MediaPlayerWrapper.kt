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
import online.hudacek.fxradio.media.player.CustomPlayer
import online.hudacek.fxradio.media.player.VLCPlayer

private val logger = KotlinLogging.logger {}

enum class PlayerType {
    Custom, VLC
}

object MediaPlayerWrapper {

    private var internalMediaPlayer: MediaPlayer? = null

    val isInitialized: Boolean
        get() = internalMediaPlayer != null

    fun init(playerType: PlayerType) {
        logger.info { "MediaPlayer $playerType initialized" }

        if (internalMediaPlayer != null) release()

        if (playerType == PlayerType.Custom) {
            internalMediaPlayer = CustomPlayer()
        } else {
            try {
                internalMediaPlayer = VLCPlayer()
            } catch (e: Exception) {
                logger.error(e) { "VLC player failed to initialize!" }
            }
        }
    }

    fun play(streamUrl: String) = internalMediaPlayer?.play(streamUrl)

    fun stop() = internalMediaPlayer?.stop()

    fun release() = internalMediaPlayer?.release()

    fun changeVolume(newVolume: Double) = internalMediaPlayer?.changeVolume(newVolume)
}