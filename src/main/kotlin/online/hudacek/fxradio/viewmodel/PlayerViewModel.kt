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

package online.hudacek.fxradio.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import io.reactivex.Observable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.data.AppNotification
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.MediaPlayerFactory
import online.hudacek.fxradio.media.StreamMetaData
import online.hudacek.fxradio.usecase.ClickUseCase
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.saveProperties
import online.hudacek.fxradio.utils.value
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.get
import tornadofx.onChange
import tornadofx.property

sealed class PlayerState {
    object Playing : PlayerState()
    object Stopped : PlayerState()
    data class Error(val cause: String) : PlayerState()
}

class Player(station: Station,
             animate: Boolean = Properties.PlayerAnimated.value(true),
             volume: Double = Properties.Volume.value(0.0),
             trackName: String = "",
             mediaPlayer: MediaPlayer = MediaPlayerFactory.create(Properties.Player.value("VLC"))) {
    var animate: Boolean by property(animate)
    var station: Station by property(station)
    var volume: Double by property(volume)
    var trackName: String by property(trackName)
    var mediaPlayer: MediaPlayer by property(mediaPlayer)
}

/**
 * Handles station playing logic
 */
class PlayerViewModel : BaseStateViewModel<Player, PlayerState>(initialState = PlayerState.Stopped) {

    private val clickUseCase: ClickUseCase by inject()

    val stationProperty = bind(Player::station) as ObjectProperty
    val animateProperty = bind(Player::animate) as BooleanProperty
    val volumeProperty = bind(Player::volume) as DoubleProperty
    val trackNameProperty = bind(Player::trackName) as StringProperty

    val mediaPlayerProperty = bind(Player::mediaPlayer) as ObjectProperty

    val stationObservable: Observable<Station> = stationProperty
            .toObservableChangesNonNull()
            .map { it.newVal }

    init {
        stationObservable
                .filter { it.isValid() }
                .doOnEach(appEvent.addToHistory) //Send the new history item event
                .flatMapSingle(clickUseCase::execute)
                .subscribe {
                    //Update the name of the station
                    trackNameProperty.value = it.name + " - " + messages["player.noMetaData"]

                    //Restart playing status
                    stateProperty.value = PlayerState.Stopped
                    stateProperty.value = PlayerState.Playing
                }

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
    }

    /**
     * Handles player state changes
     */
    override fun onNewState(newState: PlayerState) {
        if (newState is PlayerState.Playing) {
            //Ignore stations with empty stream URL
            stationProperty.value.url_resolved?.let { url ->
                mediaPlayerProperty.value?.changeVolume(volumeProperty.value)
                mediaPlayerProperty.value?.play(url)
            }
        } else {
            if (newState is PlayerState.Error) {
                appEvent.appNotification.onNext(AppNotification(newState.cause, FontAwesome.Glyph.WARNING))
            }
            mediaPlayerProperty.value?.stop()
        }
    }

    fun releasePlayer() = mediaPlayerProperty.value.release()

    fun togglePlayerState() {
        if (stateProperty.value == PlayerState.Playing) {
            stateProperty.value = PlayerState.Stopped
        } else {
            stateProperty.value = PlayerState.Playing
        }
    }

    /**
     * Save player related key/values to app.properties file
     */
    override fun onCommit() {
        saveProperties(mapOf(
                Properties.Player to mediaPlayerProperty.value.playerType,
                Properties.PlayerAnimated to animateProperty.value,
                Properties.Volume to volumeProperty.value
        ))
    }
}

