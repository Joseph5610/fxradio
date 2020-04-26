package online.hudacek.broadcastsfx.views

import de.codecentric.centerdevice.MenuToolkit
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.MenuBarController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayerTypeChange
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.utils.Utils
import tornadofx.*
import java.util.*


class MenuBarView : View() {

    private val controller: MenuBarController by inject()

    private var playerPlay: MenuItem by singleAssign()
    private var playerStop: MenuItem by singleAssign()
    private var playerCheck: CheckMenuItem by singleAssign()

    private val keyCodePlay = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
    private val keyCodeStop = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
    private val keyCodeInfo = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)

    private var currentPlayerType = controller.mediaPlayer.playerType

    private val historyMenu = Menu(messages["menu.history"])
    private val stationMenu = Menu(messages["menu.station"]).apply {
        isVisible = false

        item(messages["menu.station.info"], keyCodeInfo) {
            action {
                controller.openStationInfo()
            }
        }

        item(messages["menu.station.report"]) {
            isDisable = true
        }
    }

    private val viewMenu = Menu(messages["menu.view"]).apply {
        item(messages["menu.view.stats"]).action {
            controller.openStats()
        }
    }

    private val playerMenu = Menu(messages["menu.player.controls"]).apply {
        playerPlay = item(messages["menu.player.start"], keyCodePlay) {
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Playing))
            }
        }

        playerStop = item(messages["menu.player.stop"], keyCodeStop) {
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
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
            }
        }
    }

    init {
        controller.currentStation.station.onChange {
            stationMenu.isVisible = it != null
            playerPlay.isVisible = it != null
            playerStop.isVisible = it != null
        }

        controller.stationHistory.stations.onChange {
            if (historyMenu.items.size > 10) {
                historyMenu.items.removeAt(0)
            }
        }

        subscribe<PlayerTypeChange> { event ->
            with(event) {
                currentPlayerType = changedPlayerType
                playerCheck.isSelected = changedPlayerType == PlayerType.Native
            }
        }
    }

    override val root = if (Utils.isMacOs) {
        platformMenuBar()
    } else {
        defaultMenuBar()
    }

    private fun defaultMenuBar(): MenuBar {
        return menubar {
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
                    controller.closeApp(currentStage)
                }
            }
            menus.addAll(stationMenu, playerMenu, historyMenu, viewMenu)
        }
    }

    private fun platformMenuBar(): MenuBar {
        return menubar {
            val tk = MenuToolkit.toolkit(Locale.getDefault())
            tk.setApplicationMenu(tk.createDefaultApplicationMenu(About.appName))

            useSystemMenuBarProperty().set(true)

            val appMenu = Menu(About.appName).apply {
                item(messages["menu.app.about"]).action {
                    controller.openAbout()
                }
                separator()
                item(messages["menu.app.server"]).action {
                    controller.openServerSelect()
                }
                item(messages["menu.app.attributions"]).action {
                    controller.openAttributions()
                }
                separator()
            }
            appMenu.items.addAll(
                    tk.createHideMenuItem(About.appName), tk.createHideOthersMenuItem(), tk.createUnhideAllMenuItem(),
                    SeparatorMenuItem(), tk.createQuitMenuItem(About.appName))

            val windowMenu = Menu("Window")
            windowMenu.items.addAll(tk.createMinimizeMenuItem(), tk.createZoomMenuItem(), tk.createCycleWindowsItem(),
                    SeparatorMenuItem(), tk.createBringAllToFrontItem())

            menus.addAll(stationMenu, playerMenu, historyMenu, viewMenu, windowMenu)

            tk.setApplicationMenu(appMenu)
            tk.autoAddWindowMenuItems(windowMenu)
            tk.setMenuBar(this)
        }
    }
}