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

package online.hudacek.fxradio.views

import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.events.PlaybackChangeEvent
import online.hudacek.fxradio.events.PlayingStatus
import online.hudacek.fxradio.macos.MacMenu
import online.hudacek.fxradio.media.PlayerType
import online.hudacek.fxradio.utils.menu
import online.hudacek.fxradio.utils.shouldBeVisible
import online.hudacek.fxradio.viewmodel.MenuModel
import online.hudacek.fxradio.viewmodel.MenuViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class MenuBarView : View() {

    private val menuViewModel: MenuViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    private var playerPlay: MenuItem by singleAssign()
    private var playerStop: MenuItem by singleAssign()
    private var playerCheck: CheckMenuItem by singleAssign()
    private var playerAnimateCheck: CheckMenuItem by singleAssign()
    private var playerNotificationsCheck: CheckMenuItem by singleAssign()

    private val stationMenu = Menus.stationMenu
    private val helpMenu = Menus.helpMenu
    private val historyMenu = Menus.historyMenu

    private val keyPlay = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
    private val keyStop = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)

    private val playerMenu = menu(messages["menu.player.controls"]) {
        playerPlay = item(messages["menu.player.start"], keyPlay) {
            shouldBeVisible(playerViewModel.stationProperty)
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Playing))
            }
        }

        playerStop = item(messages["menu.player.stop"], keyStop) {
            shouldBeVisible(playerViewModel.stationProperty)
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
            }
        }

        playerCheck = checkmenuitem(messages["menu.player.switch"]) {
            isSelected = playerViewModel.playerTypeProperty.value == PlayerType.Custom
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
                playerViewModel.playerTypeProperty.value =
                        if (playerViewModel.playerTypeProperty.value == PlayerType.Custom) {
                            PlayerType.VLC
                        } else {
                            PlayerType.Custom
                        }
                playerViewModel.commit()
            }
        }

        playerAnimateCheck = checkmenuitem(messages["menu.player.animate"]) {
            bind(playerViewModel.animateProperty)
            action {
                playerViewModel.commit()
            }
        }
        playerNotificationsCheck = checkmenuitem(messages["menu.player.notifications"]) {
            bind(playerViewModel.notificationsProperty)
            action {
                playerViewModel.commit()
            }
        }
    }

    init {
        menuViewModel.item = MenuModel()

        playerViewModel.playerTypeProperty.onChange {
            playerCheck.isSelected = it == PlayerType.Custom
        }
    }

    override val root = if (menuViewModel.useNative) {
        platformMenuBar()
    } else {
        defaultMenuBar()
    }

    private fun defaultMenuBar() = menubar {
        menu(FxRadio.appName) {
            addAppMenuContent()
            item(messages["menu.app.quit"]).action {
                currentStage?.close()
                playerViewModel.releasePlayer()
            }
        }
        menus.addAll(stationMenu, playerMenu, historyMenu, helpMenu)
    }

    /**
     * Platform specific menu bar working on OSX
     * used instead of in-app menubar
     */
    private fun platformMenuBar(): MenuBar {
        return MacMenu.menuBar {
            MacMenu.appMenu {
                addAppMenuContent()
            }
        }.apply {
            menus.addAll(
                    stationMenu,
                    playerMenu,
                    historyMenu,
                    MacMenu.windowMenu(messages["macos.menu.window"]),
                    helpMenu)
        }
    }

    private fun Menu.addAppMenuContent() {
        item(messages["menu.app.about"] + " " + FxRadio.appName).action {
            menuViewModel.openAbout()
        }
        separator()
        item(messages["menu.app.attributions"]).action {
            menuViewModel.openAttributions()
        }
        separator()
    }
}