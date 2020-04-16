package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import mu.KotlinLogging
import online.hudacek.broadcastsfx.controllers.PlayerController
import online.hudacek.broadcastsfx.data.Station
import online.hudacek.broadcastsfx.events.StationChangedEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.extension.MediaPlayerWrapper
import online.hudacek.broadcastsfx.extension.cancelPlaying
import online.hudacek.broadcastsfx.extension.changeVolume
import online.hudacek.broadcastsfx.extension.play
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*

private val logger = KotlinLogging.logger {}

class PlayerView : View() {

    private val controller: PlayerController by inject()

    private var playingStation: Station? = null
    private var previousStatus = PlayingStatus.Stopped

    private lateinit var radioNameLabel: Label
    private lateinit var radioLogoBox: VBox
    private lateinit var playerControls: Button

    private val playButton = imageview("Media-Controls-Play-icon.png") {
        id = "playerControl"
        fitWidth = 30.0
        fitHeight = 30.0
        isPreserveRatio = true
    }

    private val stopButton = imageview("Media-Controls-Stop-icon.png") {
        id = "playerControl"
        fitWidth = 30.0
        fitHeight = 30.0
        isPreserveRatio = true
    }

    init {
        subscribe<StationChangedEvent> { event ->
            logger.debug { "received event " + event.playingStatus + " for " + event.station }
            radioNameLabel.text = event.station.name
            updateControls(event.playingStatus)
            if (event.playingStatus == PlayingStatus.Playing) {
                if (event.playingStatus != previousStatus || event.station.stationuuid != playingStation?.stationuuid) {
                    play(event.station.url_resolved)
                }
                updateLogo(event.station.favicon)
            } else if (event.playingStatus == PlayingStatus.Stopped) {
                cancelPlaying()
            }
            previousStatus = event.playingStatus
            playingStation = event.station
        }
    }

    private fun updateControls(status: PlayingStatus) {
        logger.debug { "updating controls" }
        if (status == PlayingStatus.Stopped) {
            playerControls.replaceChildren { add(playButton) }
        } else {
            playerControls.replaceChildren { add(stopButton) }
        }
    }

    private fun updateLogo(url: String?) {
        val iv = ImageView(Image(url, true))
        iv.fitHeight = 30.0
        iv.fitWidth = 30.0
        radioLogoBox.replaceChildren { add(iv) }
    }

    override val root = vbox {
        prefHeight = 80.0
        paddingTop = 20.0
        hbox(15) {
            alignment = Pos.CENTER_LEFT
            paddingLeft = 30.0
            playerControls = button("") {
                add(stopButton)
                action {
                    MediaPlayerWrapper.mediaPlayerCoroutine?.let {
                        if (it.isActive) {
                            fire(StationChangedEvent(playingStation!!, PlayingStatus.Stopped))
                        } else {
                            fire(StationChangedEvent(playingStation!!, PlayingStatus.Playing))
                        }
                    }
                }
            }
            region {
                hgrow = Priority.ALWAYS
            }
            vbox {
                addClass(Styles.playerBackground)
                hbox(5) {
                    radioLogoBox = vbox {
                        alignment = Pos.CENTER_LEFT
                    }
                    separator(Orientation.VERTICAL)
                    vbox {
                        paddingLeft = 10.0
                        paddingRight = 10.0
                        label("Now streaming")
                        radioNameLabel = label("-")
                    }
                }
            }
            region {
                hgrow = Priority.ALWAYS
            }
            hbox {
                paddingRight = 30.0
                alignment = Pos.CENTER_LEFT
                imageview("Media-Controls-Volume-Down-icon.png") {
                    fitWidth = 16.0
                    fitHeight = 16.0
                    isPreserveRatio = true
                }
                slider(-30..6, value = -5) {
                    valueProperty().addListener { observable, oldValue, newValue ->
                        println("Changed value to " + value)
                        MediaPlayerWrapper.audioFrame?.changeVolume(newValue.toFloat())
                    }
                }
                imageview("Media-Controls-Volume-Up-icon.png") {
                    fitWidth = 16.0
                    fitHeight = 16.0
                    isPreserveRatio = true
                }
            }
        }
    }
}