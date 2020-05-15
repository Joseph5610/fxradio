/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.broadcastsfx.views

import com.sun.javafx.PlatformUtil
import de.codecentric.centerdevice.MenuToolkit
import de.codecentric.centerdevice.dialogs.about.AboutStageBuilder
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.Broadcasts
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.controllers.MenuBarController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.extension.createImage
import online.hudacek.broadcastsfx.extension.set
import online.hudacek.broadcastsfx.extension.shouldBeDisabled
import online.hudacek.broadcastsfx.extension.shouldBeVisible
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.model.StationHistoryModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import java.util.*

class MenuBarView : View() {

    private val controller: MenuBarController by inject()
    private val notification by lazy { find(MainView::class).notification }

    private val stationHistory: StationHistoryModel by inject()
    private val player: PlayerModel by inject()

    private var playerPlay: MenuItem by singleAssign()
    private var playerStop: MenuItem by singleAssign()
    private var playerCheck: CheckMenuItem by singleAssign()
    private var playerAnimateCheck: CheckMenuItem by singleAssign()

    private val historyMenu = Menu(messages["menu.history"]).apply {
        shouldBeDisabled(player.station)
        items.bind(stationHistory.stations.value) {
            item("${it.name} (${it.countrycode})") {
                //for some reason macos native menu does not respect
                //width/height setting so it is disabled for now
                if (!PlatformUtil.isMac() || !controller.usePlatformMenuBarProperty) {
                    graphic = imageview {
                        createImage(it)
                        fitHeight = 15.0
                        fitWidth = 15.0
                        isPreserveRatio = true
                    }
                }
                action {
                    player.station.value = it
                }
            }
        }
    }

    private val stationMenu = Menu(messages["menu.station"]).apply {
        item(messages["menu.station.info"], keyInfo) {
            shouldBeDisabled(player.station)
            action {
                controller.openStationInfo()
            }
        }

        item(messages["menu.station.favourite"], keyFavourites) {
            shouldBeDisabled(player.station)
            action {
                if (player.station.value.isFavourite) {
                    notification[FontAwesome.Glyph.WARNING] = messages["menu.station.favourite.error"]
                } else {
                    player.station.value.addFavourite().subscribe { _ ->
                        notification[FontAwesome.Glyph.CHECK] = messages["menu.station.favourite.added"]
                    }
                }
            }
        }

        item(messages["menu.station.add"], keyAdd) {
            isVisible = Config.Flags.addStationEnabled
            action {
                controller.openAddNewStation()
            }
        }
    }

    private val viewMenu = Menu(messages["menu.view"]).apply {
        item(messages["menu.view.stats"]).action {
            controller.openStats()
        }
    }

    private val playerMenu = Menu(messages["menu.player.controls"]).apply {
        playerPlay = item(messages["menu.player.start"], keyPlay) {
            shouldBeVisible(player.station)
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Playing))
            }
        }

        playerStop = item(messages["menu.player.stop"], keyStop) {
            shouldBeVisible(player.station)
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
            }
        }

        playerCheck = checkmenuitem(messages["menu.player.switch"]) {
            isSelected = player.playerType.value == PlayerType.Native
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
                if (player.playerType.value == PlayerType.Native) {
                    player.playerType.value = PlayerType.VLC
                } else {
                    player.playerType.value = PlayerType.Native
                }
                player.commit()
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
        player.playerType.onChange {
            playerCheck.isSelected = it == PlayerType.Native
        }
    }


    override val root = if (controller.shouldUsePlatformMenuBar) {
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

        val aboutStageBuilder = AboutStageBuilder
                .start("")
                .withAppName(About.appName + " - " + About.appDesc)
                .withCloseOnFocusLoss()
                .withVersionString("Version ${Broadcasts.version}")
                .withCopyright("Copyright \u00A9 " + Calendar
                        .getInstance()[Calendar.YEAR] + " " + About.author)
                .withImage(Image(About.appLogo))

        val appMenu = Menu(About.appName).apply {
            addAboutMenu(tk.createAboutMenuItem(About.appName, aboutStageBuilder.build()))
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

    private fun Menu.addAboutMenu(aboutMenuItem: MenuItem? = null) {
        if (aboutMenuItem != null) {
            items.add(aboutMenuItem)
        } else {
            item(messages["menu.app.about"]).action {
                controller.openAbout()
            }
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
            confirm(messages["cache.clear.confirm"], messages["cache.clear.text"]) {
                if (controller.clearCache()) {
                    notification[FontAwesome.Glyph.CHECK] = messages["cache.clear.ok"]
                } else {
                    notification[FontAwesome.Glyph.WARNING] = messages["cache.clear.error"]
                }
            }
        }
    }

    private companion object {
        val keyPlay = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
        val keyStop = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
        val keyInfo = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)
        val keyAdd = KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)
        val keyFavourites = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)
    }
}