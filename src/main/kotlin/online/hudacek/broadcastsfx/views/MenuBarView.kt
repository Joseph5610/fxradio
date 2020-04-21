package online.hudacek.broadcastsfx.views

import javafx.application.Platform
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.MenuBarController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayerTypeChange
import online.hudacek.broadcastsfx.events.PlayingStatus
import tornadofx.*

class MenuBarView : View() {

    private val controller: MenuBarController by inject()
    private var stationInfo: Menu by singleAssign()
    private var playerPlay: MenuItem by singleAssign()
    private var playerStop: MenuItem by singleAssign()
    private var playerCheck: CheckMenuItem by singleAssign()

    private val keyCodePlay = KeyCodeCombination(KeyCode.P)
    private val keyCodeStop = KeyCodeCombination(KeyCode.S)
    private val keyCodeInfo = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)

    private var currentPlayerType = controller.mediaPlayer.playerType

    init {
        controller.currentStation.station.onChange {
            stationInfo.isDisable = it == null
            playerPlay.isDisable = it == null
            playerStop.isDisable = it == null
        }

        subscribe<PlayerTypeChange> { event ->
            with(event) {
                currentPlayerType = changedPlayerType
                playerCheck.isSelected = changedPlayerType == PlayerType.Native
            }
        }
    }

    override val root = menubar {
        menu(About.appName) {
            item(messages["menu.app.about"]).action {
                controller.openAbout()
            }
            item(messages["menu.app.server"]).action {
                controller.openServerSelect()
            }
            item(messages["menu.app.attributions"]).action {
                controller.openAttributions()
            }
            item(messages["menu.app.quit"]).action {
                Platform.exit()
            }
        }
        stationInfo = menu(messages["menu.station"]) {
            isDisable = true
            item(messages["menu.station.info"], keyCodeInfo) {
                action {
                    controller.openStationInfo()
                }
            }
            item(messages["menu.station.report"]).action {

            }
        }
        menu(messages["menu.player.controls"]) {
            playerPlay = item(messages["menu.player.start"], keyCodePlay) {
                isDisable = true
                action {
                    fire(PlaybackChangeEvent(PlayingStatus.Playing))
                }
            }

            playerStop = item(messages["menu.player.stop"], keyCodeStop) {
                isDisable = true
                action {
                    fire(PlaybackChangeEvent(PlayingStatus.Stopped))
                }
            }

            playerCheck = checkmenuitem(messages["menu.player.switch"]) {
                isSelected = currentPlayerType == PlayerType.Native
                action {
                    if (currentPlayerType == PlayerType.Native) {
                        fire(PlayerTypeChange(PlayerType.VLC))
                    } else {
                        fire(PlayerTypeChange(PlayerType.Native))
                    }
                }
            }
        }
        menu(messages["menu.view"]) {
            item(messages["menu.view.stats"]).action {
                controller.openStats()
            }
        }
    }
}