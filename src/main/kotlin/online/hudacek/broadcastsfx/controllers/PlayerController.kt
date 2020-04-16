package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.data.Station
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.events.StationChangedEvent
import online.hudacek.broadcastsfx.extension.MediaPlayerWrapper
import online.hudacek.broadcastsfx.views.PlayerView
import tornadofx.Controller

class PlayerController : Controller() {

    private val mediaPlayer = MediaPlayerWrapper

    private var playingStation: Station? = null
    private var previousStatus = PlayingStatus.Stopped

    private val view by lazy { find(PlayerView::class) }

    fun handleStationChange(event: StationChangedEvent) {
        if (event.playingStatus == PlayingStatus.Playing) {
            if (event.playingStatus != previousStatus || event.station.stationuuid != playingStation?.stationuuid) {
                event.station.url_resolved?.let { mediaPlayer.play(it) }
            }
            view.updateLogo(event.station.favicon)
        } else if (event.playingStatus == PlayingStatus.Stopped) {
            mediaPlayer.cancelPlaying()
        }
        previousStatus = event.playingStatus
        playingStation = event.station
    }

    fun handlePlayerControls() {
        if (mediaPlayer.isPlaying) {
            fire(StationChangedEvent(playingStation!!, PlayingStatus.Stopped))
        } else {
            fire(StationChangedEvent(playingStation!!, PlayingStatus.Playing))
        }
    }

    fun changeVolume(newVolume: Float) = mediaPlayer.changeVolume(newVolume)
}