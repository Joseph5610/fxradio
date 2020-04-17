package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.extension.MediaPlayerWrapper
import online.hudacek.broadcastsfx.views.PlayerView
import online.hudacek.broadcastsfx.model.StationViewModel
import tornadofx.Controller
import tornadofx.onChange

class PlayerController : Controller() {

    private val mediaPlayer = MediaPlayerWrapper

    private var previousStation: Station? = null
    private var previousStatus = PlayingStatus.Stopped

    val currentStation: StationViewModel by inject()

    init {
        currentStation.itemProperty.onChange {
            //println("view Model onChange")
            if (it != null) {
                if (it.station.stationuuid != previousStation?.stationuuid) {
                    view.updateUI(it.station)
                }
            }
        }
    }

    private val view by lazy { find(PlayerView::class) }

    fun handleStationChange(event: PlaybackChangeEvent) {
        if (event.playingStatus == PlayingStatus.Playing) {
            if (event.playingStatus != previousStatus) {
                currentStation.item.station.url_resolved?.let { mediaPlayer.play(it) }
            }

        } else if (event.playingStatus == PlayingStatus.Stopped) {
            mediaPlayer.cancelPlaying()
        }

        previousStatus = event.playingStatus
    }

    fun handlePlayerControls() {
        if (mediaPlayer.isPlaying) {
            fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        } else {
            fire(PlaybackChangeEvent(PlayingStatus.Playing))
        }
    }

    fun changeVolume(newVolume: Float) = mediaPlayer.changeVolume(newVolume)
}