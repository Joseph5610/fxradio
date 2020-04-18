package online.hudacek.broadcastsfx.views

import javafx.application.Platform
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.MenuBarController
import tornadofx.*

class MenuBarView : View() {

    private val controller: MenuBarController by inject()
    private var stationInfo: Menu by singleAssign()

    init {
        controller.currentStation.station.onChange {
            stationInfo.isDisable = it == null
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
            item(messages["menu.player.start"]).action {

            }
            item(messages["menu.player.stop"]).action {

            }
        }
        menu(messages["menu.view"]) {
            item(messages["menu.view.stats"]).action {
                controller.openStats()
            }
        }
    }
}