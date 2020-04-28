package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.model.CurrentStationModel
import tornadofx.Controller

class PlayerController : Controller() {

    val currentStation: CurrentStationModel by inject()

    val mediaPlayer by lazy { MediaPlayerWrapper }

    var playingStatus: PlayingStatus = PlayingStatus.Stopped
    var previousStation: Station? = null

    fun handlePlayerControls() {
        if (playingStatus == PlayingStatus.Playing) {
            fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        } else {
            fire(PlaybackChangeEvent(PlayingStatus.Playing))
        }
    }

    fun play(url: String) = mediaPlayer.play(url)

    fun playPreviousStation() {
        previousStation?.let {
            it.url_resolved?.let { url -> play(url) }
        }
    }
}