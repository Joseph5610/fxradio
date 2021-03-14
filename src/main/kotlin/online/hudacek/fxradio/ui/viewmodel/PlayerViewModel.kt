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

package online.hudacek.fxradio.ui.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import io.reactivex.Single
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import mu.KotlinLogging
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.ClickResponse
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.events.MetaData
import online.hudacek.fxradio.events.OsNotification
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.applySchedulers
import online.hudacek.fxradio.utils.saveProperties
import tornadofx.ItemViewModel
import tornadofx.get
import tornadofx.onChange
import tornadofx.property

private val logger = KotlinLogging.logger {}

enum class PlayerState {
    Playing, Stopped, Error
}

class Player(station: Station = Station.dummy,
             animate: Boolean = true,
             volume: Double,
             playerState: PlayerState = PlayerState.Stopped,
             trackName: String = "",
             mediaPlayer: MediaPlayer) {
    var animate: Boolean by property(animate)
    var station: Station by property(station)
    var volume: Double by property(volume)
    var playerState: PlayerState by property(playerState)
    var trackName: String by property(trackName)
    var mediaPlayer: MediaPlayer by property(mediaPlayer)
}

/**
 * Player view model
 * -------------------
 * Stores player settings, toggles playing
 * Increment station history list
 * Used all around the app
 */
class PlayerViewModel : ItemViewModel<Player>() {
    private val appEvent: AppEvent by inject()

    val stationProperty = bind(Player::station) as ObjectProperty
    val playerStateProperty = bind(Player::playerState) as ObjectProperty

    val animateProperty = bind(Player::animate) as BooleanProperty
    val volumeProperty = bind(Player::volume) as DoubleProperty
    val trackNameProperty = bind(Player::trackName) as StringProperty

    val mediaPlayerProperty = bind(Player::mediaPlayer) as ObjectProperty

    init {
        stationProperty
                .toObservableChangesNonNull()
                .map { it.newVal }
                .filter { it.isValid() }
                .doOnError { logger.error(it) { "Error with changing station..." } }
                .flatMapSingle {
                    //Restart playing status
                    playerStateProperty.value = PlayerState.Stopped
                    playerStateProperty.value = PlayerState.Playing

                    //Update the name of the station
                    trackNameProperty.value = it.name + " - " + messages["player.noMetaData"]

                    //Increase count of the station
                    StationsApi.service
                            .click(it.stationuuid)
                            .compose(applySchedulers())
                            .onErrorResumeNext {
                                //We do not care if this response fails
                                Single.just(ClickResponse(false, "Error in ClickResponse"))
                            }
                }
                .subscribe {
                    logger.debug { "Station changed, ClickResponse: $it" }
                }

        //Set volume for current player
        volumeProperty.onChange { mediaPlayerProperty.value?.changeVolume(it) }

        playerStateProperty.onChange {
            if (it == PlayerState.Playing) {
                //Ignore stations with empty stream URL
                stationProperty.value.url_resolved?.let { url ->
                    mediaPlayerProperty.value?.changeVolume(volumeProperty.value)
                    mediaPlayerProperty.value?.play(url)
                }
            } else {
                mediaPlayerProperty.value?.stop()
            }
        }

        /**
         * Called when new song starts playing or other metadata of stream changes
         */
        appEvent.playerMetaData
                .map { m -> MetaData(m.stationName.trim(), m.nowPlaying.trim()) }
                .filter { it.nowPlaying.length > 1 }
                .observeOnFx()
                .subscribe {
                    appEvent.osNotification
                            .onNext(OsNotification(title = it.nowPlaying, value = it.stationName))
                    trackNameProperty.value = it.nowPlaying
                }
    }

    fun releasePlayer() = mediaPlayerProperty.value.release()

    fun togglePlayer() {
        if (playerStateProperty.value == PlayerState.Playing) {
            playerStateProperty.value = PlayerState.Stopped
        } else {
            playerStateProperty.value = PlayerState.Playing
        }
    }

    override fun onCommit() {
        saveProperties(mapOf(
                Properties.PLAYER to mediaPlayerProperty.value.playerType,
                Properties.PLAYER_ANIMATE to animateProperty.value,
                Properties.VOLUME to volumeProperty.value
        ))
    }
}

