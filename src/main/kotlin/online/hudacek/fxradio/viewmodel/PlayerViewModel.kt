package online.hudacek.fxradio.viewmodel

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.media.MediaPlayerWrapper
import online.hudacek.fxradio.media.PlayerType
import tornadofx.*

enum class PlayingStatus {
    Playing, Stopped, Error
}

class PlayerModel(animate: Boolean = true, station: Station = Station.stub,
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

    private val stationsHistoryView: StationsHistoryViewModel by inject()

    val animateProperty = bind(PlayerModel::animate) as BooleanProperty
    val notificationsProperty = bind(PlayerModel::notifications) as BooleanProperty
    val stationProperty = bind(PlayerModel::station) as ObjectProperty
    val playerTypeProperty = bind(PlayerModel::playerType) as ObjectProperty
    val volumeProperty = bind(PlayerModel::volume) as DoubleProperty
    val playingStatusProperty = bind(PlayerModel::playingStatus) as ObjectProperty

    init {
        stationProperty.onChange {
            it?.let {
                stationsHistoryView.add(it)
                if (it.isValid()) {
                    playingStatusProperty.value = PlayingStatus.Stopped
                    playingStatusProperty.value = PlayingStatus.Playing
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
        with(app.config) {
            set(Config.Keys.playerAnimate to animateProperty.value)
            set(Config.Keys.playerType to playerTypeProperty.value)
            set(Config.Keys.notifications to notificationsProperty.value)
            set(Config.Keys.volume to volumeProperty.value)
            save()
        }
    }
}

