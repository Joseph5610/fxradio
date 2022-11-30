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

import online.hudacek.fxradio.Config
import online.hudacek.fxradio.ui.util.openUrl
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import tornadofx.action
import tornadofx.get

class HelpMenu : BaseMenu("menu.help") {

    private val logMenu: LogMenu by inject()

    private val logsFolderPath = "file://${Config.Paths.baseAppPath}"

    override val menuItems = listOf(
        item(messages["menu.app.attributions"]) {
            action {
                Modal.Attributions.open()
            }
        },
        item(messages["menu.help.openhomepage"], KeyCodes.openWebsite) {
            action {
                appMenuViewModel.openWebsite()
            }
        },
        separator(),
        item(messages["menu.help.stats"]) {
            action {
                Modal.Stats.open()
            }
        },
        item(messages["menu.help.clearCache"]) {
            action {
                appMenuViewModel.clearCache()
            }
        },
        logMenu.menu,
        separator(),
        item(messages["menu.help.logs"], KeyCodes.openLogs) {
            action {
                app.openUrl(logsFolderPath)
            }
        },
    )
}
