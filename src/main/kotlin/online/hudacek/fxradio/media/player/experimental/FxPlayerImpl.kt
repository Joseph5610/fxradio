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

package online.hudacek.fxradio.media.player.experimental

import javafx.application.Platform
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer.Status
import mu.KotlinLogging
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.StreamUnavailableException
import online.hudacek.fxradio.ui.util.msgFormat
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.FX
import tornadofx.find
import tornadofx.get
import tornadofx.onChange
import tornadofx.onChangeOnce
import tornadofx.play
import javafx.scene.media.MediaPlayer as JFXMediaPlayer

private val logger = KotlinLogging.logger {}

/**
 * Experimental player using JavaFx native media player, not fully functional
 */
class FxPlayerImpl(override val playerType: MediaPlayer.Type = MediaPlayer.Type.FX) : MediaPlayer {

    private var jfxPlayer: JFXMediaPlayer? = null

    override fun play(streamUrl: String) {
        runCatching {
            stop()
            val media = Media(streamUrl)
            jfxPlayer = createPlayer(media)
        }.onFailure {
            logger.error(it) { "Exception when playing stream!" }
        }
    }

    override fun changeVolume(newVolume: Double) {
        // Recalculate humble player volume levels to JavaFx Player
        val fxVolume: Double =
            if (newVolume < -34.5) {
                0.0
            } else {
                (newVolume + 40) / 100
            }
        jfxPlayer?.volumeProperty()?.value = fxVolume
    }

    override fun stop() {
        jfxPlayer?.stop()
    }

    override fun release() {
        jfxPlayer?.dispose()
    }

    private fun createPlayer(media: Media) = JFXMediaPlayer(media).apply {
        logger.debug { "Requested new player for ${media.source}" }

        cycleCount = JFXMediaPlayer.INDEFINITE
        isAutoPlay = true

        errorProperty().onChange {
            Platform.runLater {
                val errorMessage = FX.messages["player.streamError"].msgFormat(it!!.localizedMessage)
                find<PlayerViewModel>().stateProperty.value = PlayerState.Error(errorMessage)
            }
        }

        if (status == Status.STOPPED || status == Status.READY) {
            play()
        }
    }
}