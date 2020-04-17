package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import online.hudacek.broadcastsfx.controllers.PlayerController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.extension.smallIcon
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*

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
            with(event) {
                println("new status $playingStatus")
                if (playingStatus == PlayingStatus.Stopped) {
                    controller.mediaPlayer.cancelPlaying()
                    playButton.image = Image("Media-Controls-Play-icon.png")
                } else {
                    controller.playPreviousStation()
                    playButton.image = Image("Media-Controls-Stop-icon.png")
                }
            }
        }

        controller.currentStation.station.onChange {
            if (it != null) {
                if (it.stationuuid != controller.previousStation?.stationuuid) {
                    it.url_resolved?.let { url ->
                        controller.play(url)
                        playButton.image = Image("Media-Controls-Stop-icon.png")
                    }
                }
                it.updateView()
                controller.previousStation = it
            }
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
                    valueProperty().onChange { volume ->
                        controller.mediaPlayer.changeVolume(volume.toFloat())
                    }
                }
                smallIcon("Media-Controls-Volume-Up-icon.png")
            }
        }
    }

    private fun Station.updateView() {
        radioNameLabel.text = name
        favicon?.let {
            radioLogo.image = Image(it, true)
        }
    }
}