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

package online.hudacek.fxradio.ui.menu

import javafx.scene.control.MenuItem
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.viewmodel.DarkModeViewModel
import tornadofx.action
import tornadofx.bind
import tornadofx.get

class AboutMenu : BaseMenu(FxRadio.appName) {

    private val darkModeViewModel: DarkModeViewModel by inject()

    val aboutMainItems: List<MenuItem>
        get() = listOf(
                item(messages["menu.app.about"] + " " + FxRadio.appName) {
                    action {
                        Modal.AppInfo.open()
                    }
                },
                item(messages["menu.app.attributions"]) {
                    action {
                        Modal.Attributions.open()
                    }
                },
                item(messages["menu.app.server"]) {
                    action {
                        Modal.Servers.open()
                    }
                },
                checkMenuItem(messages["menu.app.darkmode"]) {
                    bind(darkModeViewModel.darkModeProperty)
                    action {
                        darkModeViewModel.commit()
                    }
                }
        )

    override val menuItems = mutableListOf<MenuItem>().apply {
        addAll(aboutMainItems)
        addAll(listOf(
                separator(),
                item(messages["menu.app.quit"]) {
                    action {
                        primaryStage.close()
                    }
                }
        ))
    }
}
