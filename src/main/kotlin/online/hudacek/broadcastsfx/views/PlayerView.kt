package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import mu.KotlinLogging
import online.hudacek.broadcastsfx.controllers.PlayerController
import online.hudacek.broadcastsfx.data.Station
import online.hudacek.broadcastsfx.events.StationChangedEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.extension.MediaPlayerWrapper
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*

private val logger = KotlinLogging.logger {}

class PlayerView : View() {

    private val controller: PlayerController by inject()

    private var radioNameLabel: Label by singleAssign()
    private var radioLogoBox: VBox by singleAssign()
    private var playerControls: Button by singleAssign()

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
            controller.handleStationChange(event)
            updateControls(event.playingStatus)
        }
    }

    private fun updateControls(status: PlayingStatus) {
        if (status == PlayingStatus.Stopped) {
            playerControls.replaceChildren { add(playButton) }
        } else {
            playerControls.replaceChildren { add(stopButton) }
        }
    }

    fun updateLogo(url: String?) {
        url?.let {
            val iv = imageview(url) {
                fitWidth = 30.0
                fitHeight = 30.0
            }
            radioLogoBox.replaceChildren { add(iv) }
        }
    }

    override val root = vbox {
        prefHeight = 80.0
        paddingTop = 20.0
        hbox(15) {
            alignment = Pos.CENTER_LEFT
            paddingLeft = 30.0
            playerControls = button {
                add(playButton)
                action {
                    controller.handlePlayerControls()
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
                        imageview("Clouds-icon.png") {
                            fitWidth = 30.0
                            fitHeight = 30.0
                            isPreserveRatio = true
                        }
                    }
                    separator(Orientation.VERTICAL)
                    vbox {
                        paddingLeft = 10.0
                        paddingRight = 10.0
                        label(messages["nowStreaming"])
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
                    valueProperty().addListener { _, _, newValue ->
                        controller.changeVolume(newValue.toFloat())
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