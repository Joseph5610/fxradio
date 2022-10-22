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

package online.hudacek.fxradio.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.MediaPlayerFactory
import online.hudacek.fxradio.media.StreamMetaData
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.saveProperties
import online.hudacek.fxradio.util.value
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.get
import tornadofx.onChange
import tornadofx.property

sealed class PlayerState {
    data class Playing(val url: String) : PlayerState()
    object Stopped : PlayerState()
    data class Error(val cause: String) : PlayerState()
}

class Player(
    animate: Boolean = Properties.PlayerAnimated.value(true),
    volume: Double = Properties.Volume.value(0.0),
    trackName: String = "",
    mediaPlayer: MediaPlayer = MediaPlayerFactory.create()
) {
    var animate: Boolean by property(animate)
    var volume: Double by property(volume)
    var trackName: String by property(trackName)
    var mediaPlayer: MediaPlayer by property(mediaPlayer)
}

/**
 * Handles station playing logic
 */
class PlayerViewModel : BaseStateViewModel<Player, PlayerState>(
    initialState = PlayerState.Stopped,
    initialItem = Player()
) {

    private val selectedStationViewModel: SelectedStationViewModel by inject()

    val animateProperty = bind(Player::animate) as BooleanProperty
    val volumeProperty = bind(Player::volume) as DoubleProperty
    val trackNameProperty = bind(Player::trackName) as StringProperty
    val mediaPlayerProperty = bind(Player::mediaPlayer) as ObjectProperty

    init {

        //Set volume for current player
        volumeProperty.onChange { mediaPlayerProperty.value?.changeVolume(it) }

        /**
         * Emitted when new song starts playing or other metadata of stream changes
         */
        appEvent.streamMetaDataUpdated
            .map { m -> StreamMetaData(m.stationName.trim(), m.nowPlaying.trim()) }
            .filter { it.nowPlaying.length > 1 }
            .observeOnFx()
            .subscribe {
                trackNameProperty.value = it.nowPlaying
            }

        selectedStationViewModel.stationObservable
            .subscribe({
                //Update the name of the station
                trackNameProperty.value = messages["player.noMetaData"]

                //Restart playing status
                stateProperty.value = PlayerState.Stopped
                stateProperty.value = it.url_resolved.let { it1 -> PlayerState.Playing(it1) }
            }, { t ->
                stateProperty.value = PlayerState.Error(t.localizedMessage)
            })
    }

    /**
     * Handles player state changes
     */
    override fun onNewState(newState: PlayerState) {
        if (newState is PlayerState.Playing) {
            mediaPlayerProperty.value?.let {
                it.changeVolume(volumeProperty.value)
                it.play(newState.url)
            }
        } else {
            if (newState is PlayerState.Error) {
                appEvent.appNotification.onNext(AppNotification(newState.cause, FontAwesome.Glyph.WARNING))
            }
            mediaPlayerProperty.value?.stop()
        }
    }

    override fun onError(throwable: Throwable) {
        stateProperty.value = PlayerState.Error(throwable.localizedMessage)
        super.onError(throwable)
    }

    fun releasePlayer() = mediaPlayerProperty.value.release()

    fun togglePlayerState() {
        if (selectedStationViewModel.stationProperty.value.isValid()) {
            if (stateProperty.value is PlayerState.Playing) {
                stateProperty.value = PlayerState.Stopped
            } else {
                stateProperty.value = selectedStationViewModel.stationProperty.value.url_resolved.let {
                    PlayerState.Playing(it)
                }
            }
        }
    }

    /**
     * Save player related key/values to app.properties file
     */
    override fun onCommit() {
        app.saveProperties(
            mapOf(
                Properties.Player to mediaPlayerProperty.value.playerType,
                Properties.PlayerAnimated to animateProperty.value,
                Properties.Volume to volumeProperty.value
            )
        )
    }
}

