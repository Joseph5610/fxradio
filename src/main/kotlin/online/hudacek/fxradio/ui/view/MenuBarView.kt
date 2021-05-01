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

package online.hudacek.fxradio.ui.view

import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.*
import online.hudacek.fxradio.utils.macos.MacMenu
import online.hudacek.fxradio.viewmodel.AppMenuViewModel
import tornadofx.get
import tornadofx.menubar

class MenuBarView : BaseView() {

    private val appMenuViewModel: AppMenuViewModel by inject()

    private val aboutMenu: AboutMenu by inject()
    private val historyMenu: HistoryMenu by inject()
    private val favouritesMenu: FavouritesMenu by inject()
    private val helpMenu: HelpMenu by inject()
    private val stationMenu: StationMenu by inject()
    private val playerMenu: PlayerMenu by inject()

    override val root = if (appMenuViewModel.usePlatformProperty.value) {
        platformMenuBar()
    } else {
        defaultMenuBar()
    }

    private fun defaultMenuBar() = menubar {
        menus.addAll(
                aboutMenu.menu,
                stationMenu.menu,
                playerMenu.menu,
                favouritesMenu.menu,
                historyMenu.menu,
                helpMenu.menu)
    }

    /**
     * Platform specific menu bar working on OSX
     * used instead of in-app menubar
     */
    private fun platformMenuBar() = MacMenu.menuBar {
        MacMenu.appMenu {
            items.addAll(aboutMenu.aboutMainItems)
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