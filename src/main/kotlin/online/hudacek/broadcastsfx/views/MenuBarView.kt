package online.hudacek.broadcastsfx.views

import javafx.application.Platform
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.MenuBarController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayerTypeChange
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.*
import kotlin.reflect.jvm.internal.impl.util.Check

class MenuBarView : View() {

    private val controller: MenuBarController by inject()
    private var stationInfo: Menu by singleAssign()
    private var playerPlay: MenuItem by singleAssign()
    private var playerCheck: CheckMenuItem by singleAssign()

    init {
        controller.currentStation.station.onChange {
            stationInfo.isDisable = it == null
            playerPlay.isDisable = it == null
        }

        subscribe<PlayerTypeChange> { event ->
            with(event) {
                println("PlayerTypeChange to $playerType")
                playerCheck.isSelected = playerType == PlayerType.Native
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
            item(messages["menu.app.quit"]).action {
                Platform.exit()
            }
        }
        stationInfo = menu(messages["menu.station"]) {
            isDisable = true
            item(messages["menu.station.info"]) {
                action {
                    controller.openStationInfo()
                }
            }
            item(messages["menu.station.report"]).action {

            }
        }
        menu(messages["menu.player.controls"]) {
            playerPlay = item(messages["menu.player.start"]) {
                isDisable = true
                action {

                }
            }
            item(messages["menu.player.stop"]).action {
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
            }

            playerCheck = checkmenuitem("Use Native media player") {
                isSelected = controller.mediaPlayer.isNativePlayer
                action {
                    controller.mediaPlayer.isNativePlayer = isSelected
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