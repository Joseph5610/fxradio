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
package online.hudacek.fxradio.media.player.humble

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import online.hudacek.fxradio.media.MediaPlayer

private val logger = KotlinLogging.logger {}

/**
 * Custom Audio player using Humble library
 */
class HumblePlayerImpl(override val playerType: MediaPlayer.Type = MediaPlayer.Type.Humble) : MediaPlayer {

    private val scope = MainScope()

    private val audioComponent by lazy { HumbleAudioComponent() }
    private val metaDataService = HumbleMetaDataService()

    override fun play(streamUrl: String) {
        stop() //this player should stop itself before playing new stream

        if (MediaPlayer.isMetaDataRefreshEnabled) {
            metaDataService.restartFor(streamUrl)
        }
        scope.launch {
            audioComponent.play(streamUrl)
        }
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
        audioComponent.stop()
    }

    override fun release() {
        stop()
        scope.cancel()
    }
}
