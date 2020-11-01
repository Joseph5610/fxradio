package online.hudacek.fxradio.viewmodel

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import mu.KotlinLogging
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.Properties
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.media.MediaPlayerWrapper
import online.hudacek.fxradio.media.PlayerType
import online.hudacek.fxradio.saveProperties
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.ItemViewModel
import tornadofx.get
import tornadofx.onChange
import tornadofx.property

enum class PlayingStatus {
    Playing, Stopped, Error
}

class PlayerModel(station: Station = Station.stub,
                  animate: Boolean = true,
                  playerType: PlayerType, notifications: Boolean = true,
                  volume: Double,
                  playingStatus: PlayingStatus = PlayingStatus.Stopped) {

    val animate: Boolean by property(animate)
    val notifications: Boolean by property(notifications)
    val station: Station by property(station)
    val playerType: PlayerType by property(playerType)
    val volume: Double by property(volume)
    val playingStatus: PlayingStatus by property(playingStatus)
}

/**
 * Player view model
 * -------------------
 * Stores player settings, toggles playing
 * Increment station history list
 * Used all around the app
 */
class PlayerViewModel : ItemViewModel<PlayerModel>() {

    private val logger = KotlinLogging.logger {}

    private val historyViewModel: HistoryViewModel by inject()

    val animateProperty = bind(PlayerModel::animate) as BooleanProperty
    val notificationsProperty = bind(PlayerModel::notifications) as BooleanProperty
    val stationProperty = bind(PlayerModel::station) as ObjectProperty
    val playerTypeProperty = bind(PlayerModel::playerType) as ObjectProperty
    val volumeProperty = bind(PlayerModel::volume) as DoubleProperty
    val playingStatusProperty = bind(PlayerModel::playingStatus) as ObjectProperty

    init {
        stationProperty.onChange {
            it?.let {
                if (it.isValid()) {
                    historyViewModel.add(it)

                    //Restart playing status
                    playingStatusProperty.value = PlayingStatus.Stopped
                    playingStatusProperty.value = PlayingStatus.Playing

                    //Increase count of the station
                    StationsApi.service
                            .click(it.stationuuid)
                            .compose(applySchedulers())
                            .subscribe({
                                logger.debug { "Click registered: $it" }
                            }, {
                                logger.debug { "Click registering failed, ignoring the response" }
                            })
                }
            }
        }

        playerTypeProperty.onChange {
            it?.let {
                playingStatusProperty.value = PlayingStatus.Stopped
                MediaPlayerWrapper.init(it)

                if (it == PlayerType.Custom) {
                    fire(NotificationEvent(messages["player.ffmpeg.info"]))
                }
            }
        }

        //Set volume for current player
        //Save the ViewModel after setting new value
        volumeProperty.onChange {
            MediaPlayerWrapper.changeVolume(it)
        }

        playingStatusProperty.onChange {
            if (it == PlayingStatus.Playing) {
                //Ignore stations with empty stream URL
                stationProperty.value.url_resolved?.let { url ->
                    with(MediaPlayerWrapper) {
                        if (isInitialized) {
                            changeVolume(volumeProperty.value)
                            play(url)
                        } else {
                            //Error while initializing player
                            fire(NotificationEvent(messages["player.init.error"]))
                        }
                    }
                }
            } else {
                MediaPlayerWrapper.stop()
            }
        }
    }

    fun releasePlayer() = MediaPlayerWrapper.release()

    fun togglePlayer() {
        if (playingStatusProperty.value == PlayingStatus.Playing) {
            playingStatusProperty.value = PlayingStatus.Stopped
        } else {
            playingStatusProperty.value = PlayingStatus.Playing
        }
    }

    override fun onCommit() {
        //Save API server
        saveProperties(
                listOf(
                        Pair(Properties.PLAYER_ANIMATE, animateProperty.value),
                        Pair(Properties.PLAYER, playerTypeProperty.value),
                        Pair(Properties.NOTIFICATIONS, notificationsProperty.value),
                        Pair(Properties.VOLUME, volumeProperty.value)
                )
        )
    }
}

