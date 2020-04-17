package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import mu.KotlinLogging
import online.hudacek.broadcastsfx.controllers.PlayerController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.extension.smallIcon
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*

private val logger = KotlinLogging.logger {}

class PlayerView : View() {

    private val controller: PlayerController by inject()

    private var radioNameLabel: Label by singleAssign()
    private var radioLogo: ImageView by singleAssign()
    private var volumeSlider: Slider by singleAssign()

    private val playButton = imageview("Media-Controls-Play-icon.png") {
        fitWidth = 30.0
        fitHeight = 30.0
        isPreserveRatio = true
    }

    init {
        subscribe<PlaybackChangeEvent> { event ->
            controller.handleStationChange(event)
        }
    }

    fun updateControls(isPlaying: Boolean) {
        if (isPlaying) {
            playButton.image = Image("Media-Controls-Stop-icon.png")
        } else {
            playButton.image = Image("Media-Controls-Play-icon.png")
        }
    }

    fun updateUI(station: Station) {
        radioNameLabel.text = station.name
        station.favicon?.let {
            radioLogo.image = Image(it, true)
        }
    }

    override val root = vbox {
        prefHeight = 80.0
        paddingTop = 20.0
        hbox(15) {
            alignment = Pos.CENTER_LEFT
            paddingLeft = 30.0
            button {
                add(playButton)
                action {
                    controller.handlePlayerControls()
                }
            }
            region {
                hgrow = Priority.ALWAYS
            }
            vbox {
                addClass(Styles.playerStationInfo)
                hbox(5) {
                    vbox {
                        alignment = Pos.CENTER_LEFT
                        radioLogo = imageview("Clouds-icon.png") {
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
                smallIcon("Media-Controls-Volume-Down-icon.png")
                volumeSlider = slider(-30..6, value = -5) {
                    valueProperty().addListener { _, _, newValue ->
                        controller.changeVolume(newValue.toFloat())
                    }
                }
                smallIcon("Media-Controls-Volume-Up-icon.png")
            }
        }
    }
}