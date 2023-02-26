/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.ui.view

import javafx.scene.control.MenuBar
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.AboutMenu
import online.hudacek.fxradio.ui.menu.FavouritesMenu
import online.hudacek.fxradio.ui.menu.FileMenu
import online.hudacek.fxradio.ui.menu.HelpMenu
import online.hudacek.fxradio.ui.menu.PlayerMenu
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.util.macos.NSMenu
import online.hudacek.fxradio.util.macos.NSMenuBar
import online.hudacek.fxradio.viewmodel.AppMenuViewModel
import tornadofx.addClass
import tornadofx.get
import tornadofx.menubar

class MenuBarView : BaseView() {

    private val appMenuViewModel: AppMenuViewModel by inject()

    private val aboutMenu: AboutMenu by inject()
    private val favouritesMenu: FavouritesMenu by inject()
    private val helpMenu: HelpMenu by inject()
    private val fileMenu: FileMenu by inject()
    private val playerMenu: PlayerMenu by inject()

    private val isMenuEnabled by lazy { !(app as FxRadio).isAppRunningInTest }

    override val root = if (appMenuViewModel.usePlatformProperty.value && isMenuEnabled) {
        platformMenuBar()
    } else {
        defaultMenuBar()
    }

    private fun defaultMenuBar() = menubar {
        menus.addAll(
            aboutMenu.menu,
            fileMenu.menu,
            playerMenu.menu,
            favouritesMenu.menu,
            helpMenu.menu
        )
        addClass(Styles.mainMenuBox)
    }

    /**
     * Platform specific menu bar working on macOS
     * used instead of in-app menubar
     */
    private fun platformMenuBar() = MenuBar()
}
