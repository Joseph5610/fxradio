package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import mu.KotlinLogging
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.PlayerController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.extension.smallIcon
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*
import kotlin.contracts.contract

class PlayerView : View() {

    private val logger = KotlinLogging.logger {}

    private val controller: PlayerController by inject()

    private var radioNameLabel: Label by singleAssign()
    private var radioLogo: ImageView by singleAssign()
    private var volumeSlider: Slider by singleAssign()
    private var playerControls: Button by singleAssign()

    private var radioNameLabelParent = hbox(5) {
        vbox {
            alignment = Pos.CENTER_LEFT
            radioLogo = imageview(About.appIcon) {
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

    private val playButton = imageview("Media-Controls-Play-icon.png") {
        fitWidth = 30.0
        fitHeight = 30.0
        isPreserveRatio = true
    }

    init {
        subscribe<PlaybackChangeEvent> { event ->
            with(event) {
                logger.debug { "received PlayerChangeEvent $playingStatus" }
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
                playerControls.isDisable = false
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
            playerControls = button {
                isDisable = true
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
                add(radioNameLabelParent)
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
                        controller.mediaPlayer.changeVolume(volume)
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