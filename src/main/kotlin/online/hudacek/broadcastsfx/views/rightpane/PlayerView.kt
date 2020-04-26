package online.hudacek.broadcastsfx.views.rightpane

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.PlayerController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.ui.smallIcon
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.TickerView
import online.hudacek.broadcastsfx.ui.createImage
import online.hudacek.broadcastsfx.ui.requestFocusOnSceneAvailable
import tornadofx.*

class PlayerView : View() {

    private val controller: PlayerController by inject()

    private var radioNameLabel: TickerView by singleAssign()
    private var radioLogo: ImageView by singleAssign()

    private val nowStreamingLabel = label(messages["streamingStopped"]) {
        addClass(Styles.grayLabel)
    }

    private val playImage = imageview(playIcon) {
        fitWidth = 30.0
        fitHeight = 30.0
        isPreserveRatio = true
    }

    private val playerControls = button {
        isDisable = true
        add(playImage)
        addClass(Styles.playerControls)
        action {
            controller.handlePlayerControls()
        }
    }

    private val volumeSlider = slider(-30..5) {
        value = controller.mediaPlayer.volume
        valueProperty().onChange { newVolume ->
            controller.mediaPlayer.volume = newVolume
        }
    }

    init {
        keyboard {
            addEventHandler(KeyEvent.KEY_PRESSED) {
                if (it.code == KeyCode.SPACE) {
                    controller.handlePlayerControls()
                }
            }
        }

        subscribe<PlaybackChangeEvent> { event ->
            togglePlayerStatus(event.playingStatus)
        }

        controller.currentStation.station.onChange {
            it?.let {
                playerControls.isDisable = false
                if (it != controller.previousStation) {
                    it.url_resolved?.let { url ->
                        controller.play(url)
                        playImage.image = Image(stopIcon)
                    }
                }
                it.updateView()
                controller.previousStation = it
            }
        }
    }

    override val root = vbox {
        requestFocusOnSceneAvailable()
        prefHeight = 75.0
        paddingTop = 20.0

        hbox(15) {
            alignment = Pos.CENTER_LEFT
            paddingLeft = 30.0
            add(playerControls)
            region {
                hgrow = Priority.ALWAYS
            }

            //Player box
            hbox(5) {
                addClass(Styles.playerStationInfo)

                vbox(alignment = Pos.CENTER_LEFT) {
                    radioLogo = imageview(About.appIcon) {
                        effect = DropShadow(20.0, Color.WHITE)
                        fitWidth = 30.0
                        fitHeight = 30.0
                        isPreserveRatio = true
                    }
                }

                separator(Orientation.VERTICAL)
                vbox(alignment = Pos.CENTER) {
                    fitToParentWidth()
                    add(TickerView::class) {
                        radioNameLabel = this
                    }
                    add(nowStreamingLabel)
                }
            }
            region {
                hgrow = Priority.ALWAYS
            }
            hbox {
                paddingRight = 30.0
                alignment = Pos.CENTER_LEFT
                smallIcon(volumeDownIcon)
                add(volumeSlider)
                smallIcon(volumeUpIcon)
            }
        }
    }

    private fun togglePlayerStatus(playingStatus: PlayingStatus) {
        controller.playingStatus = playingStatus
        if (playingStatus == PlayingStatus.Stopped) {
            playImage.image = Image(playIcon)
            nowStreamingLabel.text = messages["streamingStopped"]
        } else {
            controller.playPreviousStation()
            playImage.image = Image(stopIcon)
            nowStreamingLabel.text = messages["nowStreaming"]
        }
    }

    private fun Station.updateView() {
        nowStreamingLabel.text = messages["nowStreaming"]
        radioNameLabel.updateText(name)
        radioLogo.createImage(this)
    }

    private companion object {
        private const val playIcon = "Media-Controls-Play-icon.png"
        private const val stopIcon = "Media-Controls-Stop-icon.png"
        private const val volumeDownIcon = "Media-Controls-Volume-Down-icon.png"
        private const val volumeUpIcon = "Media-Controls-Volume-Up-icon.png"
    }
}