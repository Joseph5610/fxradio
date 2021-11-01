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
package online.hudacek.fxradio.media.player.humble

import mu.KotlinLogging
import online.hudacek.fxradio.media.MediaPlayer

private val logger = KotlinLogging.logger {}

/**
 * Custom Audio player using Humble library
 */
class HumblePlayerImpl(override val playerType: MediaPlayer.Type = MediaPlayer.Type.Humble) : MediaPlayer {

    private val audioComponent = HumbleAudioComponent()
    private val metaDataService = HumbleMetaDataService()

    override fun play(streamUrl: String) {
        stop() //this player should stop itself before playing new stream

        if (MediaPlayer.isMetaDataRefreshEnabled) {
            metaDataService.restartFor(streamUrl)
        }
        audioComponent.play(streamUrl)
    }

    override fun changeVolume(newVolume: Double) {
        return try {
            audioComponent.setVolume(newVolume)
        } catch (e: Exception) {
            logger.debug { "Can't change volume to: $newVolume" }
        }
    }

    override fun stop() {
        if (MediaPlayer.isMetaDataRefreshEnabled) {
            metaDataService.cancel()
        }
        audioComponent.cancel()
    }

    override fun release() = stop() //Release is in fact same as stopping the playing in this player
}