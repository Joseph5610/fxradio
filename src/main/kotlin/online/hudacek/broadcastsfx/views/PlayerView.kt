package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.controllers.PlayerController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.model.Player
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.*
import online.hudacek.broadcastsfx.ui.createImage
import online.hudacek.broadcastsfx.ui.smallIcon
import tornadofx.*

class PlayerView : View() {

    private val controller: PlayerController by inject()
    private val player: PlayerModel by inject()

    private val radioNameTicker = TickerView()
    private val radioNameStaticText = label()

    private var radioNameContainer: VBox by singleAssign()
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
        requestFocusOnSceneAvailable()
        shouldBeDisabled(player.station)
        add(playImage)
        addClass(Styles.playerControls)
        action {
            controller.handlePlayerControls()
        }
    }

    private val volumeSlider = slider(-30..5) {
        value = controller.getVolume()
        valueProperty().onChange { newVolume ->
            controller.changeVolume(newVolume)
        }
    }

    init {
        val animateName = app.config.boolean(Config.playerAnimate, true)
        val playerType = PlayerType.valueOf(app.config.string(Config.playerType, "VLC"))
        player.item = Player(animateName, playerType = playerType)

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

        player.station.onChange {
            it?.updateView()
        }

        player.animate.onChange {
            if (it == true) {
                radioNameTicker.play()
                radioNameContainer.replaceChildren(radioNameTicker)
            } else {
                radioNameTicker.stop()
                radioNameContainer.replaceChildren(radioNameStaticText)
            }
            player.station.value.updateView()
        }
    }

    override val root = vbox {
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
                    radioNameContainer = vbox(alignment = Pos.CENTER) {
                        if (player.animate.value) {
                            add(radioNameTicker)
                        } else {
                            add(radioNameStaticText)
                        }
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
        if (playingStatus == PlayingStatus.Stopped) {
            playImage.image = Image(playIcon)
            nowStreamingLabel.text = messages["streamingStopped"]
        } else {
            playImage.image = Image(stopIcon)
            nowStreamingLabel.text = messages["nowStreaming"]
        }
    }

    private fun Station.updateView() {
        togglePlayerStatus(PlayingStatus.Playing)
        if (player.animate.value) radioNameTicker.updateText(name)
        else radioNameStaticText.text = name
        radioLogo.createImage(this)
    }

    //Icon constants
    private companion object {
        private const val playIcon = "Media-Controls-Play-icon.png"
        private const val stopIcon = "Media-Controls-Stop-icon.png"
        private const val volumeDownIcon = "Media-Controls-Volume-Down-icon.png"
        private const val volumeUpIcon = "Media-Controls-Volume-Up-icon.png"
    }
}