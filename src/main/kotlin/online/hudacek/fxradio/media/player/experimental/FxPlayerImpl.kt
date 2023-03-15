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

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer.Status
import mu.KotlinLogging
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.StreamUnavailableException
import tornadofx.onChangeOnce
import javafx.scene.media.MediaPlayer as JFXMediaPlayer

private val logger = KotlinLogging.logger {}

/**
 * Experimental player using JavaFx native media player, not fully functional
 */
class FxPlayerImpl(override val playerType: MediaPlayer.Type = MediaPlayer.Type.FX) : MediaPlayer {

    private var jfxPlayer: JFXMediaPlayer? = null

    override fun play(streamUrl: String) {
        try {
            stop()
            val media = Media(streamUrl)
            media.setOnError {
                throw StreamUnavailableException("Invalid media file!")
            }

            jfxPlayer = JFXMediaPlayer(media).apply {
                cycleCount = JFXMediaPlayer.INDEFINITE

                logger.debug { "Requested play of ${media.source}" }

                statusProperty().onChangeOnce {
                    logger.debug { "Player status change: $it" }
                    if (status == Status.STOPPED || status == Status.READY) {
                        play()
                    }
                }

                setOnError {
                    throw StreamUnavailableException("The stream cannot be played!")
                }

                setOnHalted {
                    throw StreamUnavailableException("The stream cannot be played!")
                }

                if (status == Status.STOPPED || status == Status.READY) {
                    play()
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Exception when playing stream!" }
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
}