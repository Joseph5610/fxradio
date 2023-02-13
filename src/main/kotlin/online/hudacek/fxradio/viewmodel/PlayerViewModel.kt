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
import online.hudacek.fxradio.usecase.station.StationClickUseCase
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
class PlayerViewModel : BaseStateViewModel<Player, PlayerState>(Player(), PlayerState.Stopped) {

    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val stationClickUseCase: StationClickUseCase by inject()

    val animateProperty = bind(Player::animate) as BooleanProperty
    val volumeProperty = bind(Player::volume) as DoubleProperty
    val trackNameProperty = bind(Player::trackName) as StringProperty
    val mediaPlayerProperty = bind(Player::mediaPlayer) as ObjectProperty

    fun initializePlayer() {

        // Set volume for current player
        volumeProperty.onChange { mediaPlayerProperty.value?.changeVolume(it) }

        /**
         * Emitted when new song starts playing or other metadata of stream changes
         */
        appEvent.streamMetaDataUpdates
            .map { m -> StreamMetaData(m.stationName.trim(), m.nowPlaying.trim()) }
            .filter { it.nowPlaying.length > 1 }
            .observeOnFx()
            .subscribe {
                trackNameProperty.value = it.nowPlaying
            }

        selectedStationViewModel.stationObservable
            .flatMapSingle(stationClickUseCase::execute)
            .subscribe({
                // Update the name of the station
                appEvent.streamMetaDataUpdates.onNext(StreamMetaData(it.name, messages["player.noMetaData"]))
                stateProperty.value = PlayerState.Playing(it.url)
            }, { t ->
                stateProperty.value = PlayerState.Error(t.localizedMessage)
            })

        stateObservable.subscribe {
            when (it) {
                is PlayerState.Error -> {
                    appEvent.appNotification.onNext(AppNotification(it.cause, FontAwesome.Glyph.WARNING))
                }

                is PlayerState.Playing -> {
                    mediaPlayerProperty.value?.let { mp ->
                        mp.changeVolume(volumeProperty.value)
                        mp.play(it.url)
                        mp.changeVolume(volumeProperty.value)
                    }
                }

                is PlayerState.Stopped -> mediaPlayerProperty.value?.stop()
            }
        }
    }

    /**
     * Handles player state changes
     */

    fun togglePlayerState() {
        if (!selectedStationViewModel.stationProperty.value.isValid()) return

        with(stateProperty) {
            value = if (value is PlayerState.Playing) {
                PlayerState.Stopped
            } else {
                selectedStationViewModel.streamUrlProperty.let {
                    PlayerState.Playing(it.value)
                }
            }
        }
    }

    fun releasePlayer() = mediaPlayerProperty.value.release()

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

