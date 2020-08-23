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

package online.hudacek.fxradio.views.menubar

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
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.controllers.menubar.MenuBarController
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.events.PlaybackChangeEvent
import online.hudacek.fxradio.events.PlayerType
import online.hudacek.fxradio.events.PlayingStatus
import online.hudacek.fxradio.extension.menu
import online.hudacek.fxradio.extension.shouldBeVisible
import online.hudacek.fxradio.viewmodel.PlayerModel
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.tools.Platform
import tornadofx.*
import java.util.*

class MenuBarView : View() {

    private val controller: MenuBarController by inject()
    private val playerModel: PlayerModel by inject()

    private var playerPlay: MenuItem by singleAssign()
    private var playerStop: MenuItem by singleAssign()
    private var playerCheck: CheckMenuItem by singleAssign()
    private var playerAnimateCheck: CheckMenuItem by singleAssign()
    private var playerNotificationsCheck: CheckMenuItem by singleAssign()

    private val usePlatformMenuBarProperty = app.config.boolean(Config.Keys.useNativeMenuBar, true)
    private val shouldUsePlatformMenuBar = Platform.getCurrent() == Platform.OSX && usePlatformMenuBarProperty

    private val stationMenu = StationMenu().menu
    private val helpMenu = HelpMenu().menu
    private val historyMenu = HistoryMenu().menu

    private val keyPlay = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
    private val keyStop = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)

    private val playerMenu = menu(messages["menu.player.controls"]) {
        playerPlay = item(messages["menu.player.start"], keyPlay) {
            shouldBeVisible(playerModel.stationProperty)
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Playing))
            }
        }

        playerStop = item(messages["menu.player.stop"], keyStop) {
            shouldBeVisible(playerModel.stationProperty)
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
            }
        }

        playerCheck = checkmenuitem(messages["menu.player.switch"]) {
            isSelected = playerModel.playerType.value == PlayerType.FFmpeg
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
                playerModel.playerType.value =
                        if (playerModel.playerType.value == PlayerType.FFmpeg) {
                            PlayerType.VLC
                        } else {
                            PlayerType.FFmpeg
                        }
                playerModel.commit()
            }
        }

        playerAnimateCheck = checkmenuitem(messages["menu.player.animate"]) {
            bind(playerModel.animate)
            action {
                playerModel.commit()
            }
        }
        playerNotificationsCheck = checkmenuitem(messages["menu.player.notifications"]) {
            bind(playerModel.notifications)
            action {
                playerModel.commit()
            }
        }
    }

    init {
        playerModel.playerType.onChange {
            playerCheck.isSelected = it == PlayerType.FFmpeg
        }
    }

    override val root = if (shouldUsePlatformMenuBar) {
        platformMenuBar()
    } else {
        defaultMenuBar()
    }

    private fun defaultMenuBar() = menubar {
        menu(FxRadio.appName) {
            item(messages["menu.app.about"]).action {
                controller.openAbout()
            }
            addAboutMenuContent()
            item(messages["menu.app.quit"]).action {
                controller.closeApp(currentStage)
            }
        }
        menus.addAll(stationMenu, playerMenu, historyMenu, helpMenu)
    }

    /**
     * Platform specific menu bar working on OSX
     * used instead of in-app menubar
     */
    private fun platformMenuBar() = menubar {
        val tk = MenuToolkit.toolkit(Locale.getDefault())
        tk.setApplicationMenu(tk.createDefaultApplicationMenu(FxRadio.appName))

        useSystemMenuBarProperty().set(true)

        val macAboutStage = AboutStageBuilder
                .start("")
                .withAppName(FxRadio.appName + " - " + FxRadio.appDesc)
                .withCloseOnFocusLoss()
                .withVersionString("Version ${FxRadio.version}")
                .withCopyright("Copyright \u00A9 " + Calendar
                        .getInstance()[Calendar.YEAR] + " " + FxRadio.author)
                .withImage(Image(Config.R.appLogo))
                .build()

        val aboutMenu = Menu(FxRadio.appName).apply {
            items.add(tk.createAboutMenuItem(FxRadio.appName, macAboutStage))
            separator()
            addAboutMenuContent()
            items.addAll(
                    tk.createHideMenuItem(FxRadio.appName),
                    tk.createHideOthersMenuItem(),
                    tk.createUnhideAllMenuItem(),
                    SeparatorMenuItem(),
                    tk.createQuitMenuItem(FxRadio.appName))
        }

        val windowMenu = Menu(messages["macos.menu.window"]).apply {
            items.addAll(
                    tk.createMinimizeMenuItem(),
                    tk.createZoomMenuItem(),
                    tk.createCycleWindowsItem(),
                    SeparatorMenuItem(),
                    tk.createBringAllToFrontItem())
        }

        menus.addAll(stationMenu, playerMenu, historyMenu, windowMenu, helpMenu)

        tk.setApplicationMenu(aboutMenu)
        tk.autoAddWindowMenuItems(windowMenu)
        tk.setMenuBar(this)
    }

    private fun Menu.addAboutMenuContent() {
        separator()
        item(messages["menu.app.server"]).action {
            controller.openServerSelect()
        }
        item(messages["menu.app.attributions"]).action {
            controller.openAttributions()
        }
        separator()
    }

    fun showVoteResult(result: Boolean) {
        if (result) {
            fire(NotificationEvent(messages["vote.ok"], FontAwesome.Glyph.CHECK))
        } else {
            fire(NotificationEvent(messages["vote.error"]))
        }
    }
}