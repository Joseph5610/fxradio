package online.hudacek.broadcastsfx.views

import de.codecentric.centerdevice.MenuToolkit
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.broadcastsfx.*
import online.hudacek.broadcastsfx.controllers.MenuBarController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayerTypeChange
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.model.CurrentStation
import online.hudacek.broadcastsfx.model.StationHistoryModel
import online.hudacek.broadcastsfx.model.CurrentStationModel
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.ui.createImage
import online.hudacek.broadcastsfx.ui.shouldBeDisabled
import online.hudacek.broadcastsfx.ui.shouldBeVisible
import tornadofx.*
import java.util.*

class MenuBarView : View() {

    private val controller: MenuBarController by inject()

    private val currentStation: CurrentStationModel by inject()
    private val stationHistory: StationHistoryModel by inject()
    private val player: PlayerModel by inject()

    private var playerPlay: MenuItem by singleAssign()
    private var playerStop: MenuItem by singleAssign()
    private var playerCheck: CheckMenuItem by singleAssign()
    private var playerAnimateCheck: CheckMenuItem by singleAssign()

    private var currentPlayerType = controller.mediaPlayer.playerType

    private val historyMenu = Menu(messages["menu.history"]).apply {
        shouldBeDisabled(currentStation.station)
        items.bind(stationHistory.stations.value) {
            item("${it.name} (${it.countrycode})") {
                //for some reason macos native menu does not respect
                // width/height setting so it is disabled for nowł
                if (!Utils.isMacOs || !shouldUseNativeMenuBar) {
                    graphic = imageview {
                        createImage(it)
                        fitHeight = 15.0
                        fitWidth = 15.0
                        isPreserveRatio = true
                    }
                }
                action {
                    currentStation.item = CurrentStation(it)
                }
            }
        }
    }

    private val stationMenu = Menu(messages["menu.station"]).apply {
        shouldBeDisabled(currentStation.station)
        item(messages["menu.station.info"], keyInfo).action {
            controller.openStationInfo()
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
        playerPlay = item(messages["menu.player.start"], keyPlay) {
            shouldBeVisible(currentStation.station)
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Playing))
            }
        }

        playerStop = item(messages["menu.player.stop"], keyStop) {
            shouldBeVisible(currentStation.station)
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

        playerAnimateCheck = checkmenuitem(messages["menu.player.animate"]) {
            isSelected = player.animate.value
            action {
                player.animate.value = !player.animate.value
                player.commit()
            }
        }
    }

    init {
        subscribe<PlayerTypeChange> { event ->
            with(event) {
                currentPlayerType = changedPlayerType
                playerCheck.isSelected = changedPlayerType == PlayerType.Native
            }
        }
    }

    private val shouldUseNativeMenuBar = app.config.boolean(Config.useNativeMenuBar, true)

    override val root = if (Utils.isMacOs && shouldUseNativeMenuBar) {
        platformMenuBar()
    } else {
        defaultMenuBar()
    }

    private fun defaultMenuBar() = menubar {
        menu(About.appName) {
            addAboutMenu()
            separator()
            item(messages["menu.app.quit"]).action {
                controller.closeApp(currentStage)
            }
        }
        menus.addAll(stationMenu, playerMenu, historyMenu, viewMenu)
    }

    /**
     * Platform specific menu bar working on OSX
     * used instead of in-app menubar
     */
    private fun platformMenuBar() = menubar {
        val tk = MenuToolkit.toolkit(Locale.getDefault())
        tk.setApplicationMenu(tk.createDefaultApplicationMenu(About.appName))

        useSystemMenuBarProperty().set(true)

        val appMenu = Menu(About.appName).apply {
            addAboutMenu()
            separator()
            items.addAll(
                    tk.createHideMenuItem(About.appName), tk.createHideOthersMenuItem(), tk.createUnhideAllMenuItem(),
                    SeparatorMenuItem(), tk.createQuitMenuItem(About.appName))
        }

        val windowMenu = Menu("Window").apply {
            items.addAll(
                    tk.createMinimizeMenuItem(),
                    tk.createZoomMenuItem(),
                    tk.createCycleWindowsItem(),
                    SeparatorMenuItem(),
                    tk.createBringAllToFrontItem())
        }

        menus.addAll(stationMenu, playerMenu, historyMenu, viewMenu, windowMenu)

        tk.setApplicationMenu(appMenu)
        tk.autoAddWindowMenuItems(windowMenu)
        tk.setMenuBar(this)
    }

    private fun Menu.addAboutMenu() {
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
        item(messages["menu.app.clearCache"]).action {
            controller.clearCache()
        }
    }

    private companion object {
        val keyPlay = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
        val keyStop = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
        val keyInfo = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)
    }
}