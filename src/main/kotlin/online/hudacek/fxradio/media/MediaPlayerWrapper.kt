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
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.media.players.CustomPlayer
import online.hudacek.fxradio.media.players.VLCPlayer
import tornadofx.*

enum class PlayerType {
    Custom, VLC
}

object MediaPlayerWrapper : Component(), MediaPlayer {

    private val logger = KotlinLogging.logger {}

    private var internalMediaPlayer: MediaPlayer? = null

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
                fire(NotificationEvent(messages["player.vlc.error"]))
            }
        }
    }

    override fun play(streamUrl: String) {
        internalMediaPlayer?.play(streamUrl)
    }

    override fun stop() {
        internalMediaPlayer?.stop()
    }

    override fun release() {
        internalMediaPlayer?.release()
    }

    override fun changeVolume(newVolume: Double) = internalMediaPlayer?.changeVolume(newVolume) ?: false
}