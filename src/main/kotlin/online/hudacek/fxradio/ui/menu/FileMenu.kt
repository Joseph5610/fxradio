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

import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.util.openInternalWindow
import tornadofx.action
import tornadofx.get

class FileMenu : BaseMenu("menu.file") {

    override val menuItems = listOf(
            item(messages["menu.station.add"], KeyCodes.newStation) {
                action {
                    Modal.AddNewStation.openInternalWindow()
                }
            },
            separator(),
            item(messages["menu.stream"], KeyCodes.openStream) {
                action {
                    Modal.OpenStream.open()
                }
            }
    )
}
