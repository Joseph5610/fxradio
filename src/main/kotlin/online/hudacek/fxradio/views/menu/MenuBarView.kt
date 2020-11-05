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

package online.hudacek.fxradio.views.menu

import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.macos.MacMenu
import online.hudacek.fxradio.utils.VersionCheck
import online.hudacek.fxradio.viewmodel.MenuModel
import online.hudacek.fxradio.viewmodel.MenuViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class MenuBarView : View() {

    private val menuViewModel: MenuViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    private val historyMenu: HistoryMenu by inject()
    private val favouritesMenu: FavouritesMenu by inject()
    private val helpMenu: HelpMenu by inject()
    private val stationMenu: StationMenu by inject()
    private val playerMenu: PlayerMenu by inject()

    init {
        menuViewModel.item = MenuModel()
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
        menus.addAll(stationMenu.menu,
                playerMenu.menu,
                favouritesMenu.menu,
                historyMenu.menu,
                helpMenu.menu)
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
            menus.addAll(stationMenu.menu,
                    playerMenu.menu,
                    favouritesMenu.menu,
                    historyMenu.menu,
                    MacMenu.windowMenu(messages["macos.menu.window"]),
                    helpMenu.menu)
        }
    }

    private fun Menu.addAppMenuContent() {
        item(messages["menu.app.about"] + " " + FxRadio.appName).action {
            menuViewModel.openAbout()
        }
        if (Config.Flags.enableServerSelection) {
            separator()
            item(messages["menu.app.server"]).action {
                menuViewModel.openAvailableServer()
            }
        }
        separator()
        if (Config.Flags.enableVersionCheck) {
            item(messages["menu.help.vcs.check"]).action {
                VersionCheck.perform()
            }
        }
        separator()
    }
}