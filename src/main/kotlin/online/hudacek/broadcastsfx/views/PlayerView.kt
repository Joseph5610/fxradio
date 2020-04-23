package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Slider
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import mu.KotlinLogging
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.PlayerController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.ui.smallIcon
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.TickerView
import online.hudacek.broadcastsfx.ui.createImage
import tornadofx.*

class PlayerView : View() {

    private val logger = KotlinLogging.logger {}

    private val controller: PlayerController by inject()

    private var radioNameLabel: TickerView by singleAssign()
    private var radioLogo: ImageView by singleAssign()
    private var volumeSlider: Slider by singleAssign()
    private var playerControls: Button by singleAssign()

    private val playButton = imageview("Media-Controls-Play-icon.png") {
        fitWidth = 30.0
        fitHeight = 30.0
        isPreserveRatio = true
    }

    init {
        subscribe<PlaybackChangeEvent> { event ->
            with(event) {
                logger.debug { "received PlayerChangeEvent $playingStatus" }
                controller.playingStatus = playingStatus

                if (playingStatus == PlayingStatus.Stopped) {
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
                if (it != controller.previousStation) {
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
        prefHeight = 75.0
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

            //Player box
            hbox(5) {
                prefWidth = 220.0
                addClass(Styles.playerStationInfo)

                vbox {
                    alignment = Pos.CENTER_LEFT
                    radioLogo = imageview(About.appIcon) {
                        effect = DropShadow(20.0, Color.WHITE)
                        fitWidth = 30.0
                        fitHeight = 30.0
                        isPreserveRatio = true
                    }
                }
                separator(Orientation.VERTICAL)

                vbox {
                    fitToParentWidth()
                    alignment = Pos.CENTER
                    add(TickerView::class) {
                        radioNameLabel = this
                    }
                    label(messages["nowStreaming"]) {
                        addClass(Styles.grayLabel)
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
                volumeSlider = slider(-30..6) {
                    value = controller.mediaPlayer.volume
                    valueProperty().onChange { newVolume ->
                        controller.mediaPlayer.volume = newVolume
                    }
                }
                smallIcon("Media-Controls-Volume-Up-icon.png")
            }
        }
    }

    private fun Station.updateView() {
        radioNameLabel.updateText(name)
        createImage(radioLogo, this)
    }
}