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

import online.hudacek.fxradio.ui.update
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.action
import tornadofx.get

class StationMenu : BaseMenu("menu.station") {

    private val playerViewModel: PlayerViewModel by inject()

    override val menuItems = listOf(
            item(messages["menu.station.info"], KeyCodes.info) {
                disableWhenInvalidStation(playerViewModel.stationProperty)
                action {
                    Modal.StationInfo.open()
                }
            },
            item(messages["copy.stream.url"]) {
                disableWhenInvalidStation(playerViewModel.stationProperty)
                action {
                    playerViewModel.stationProperty.value.url_resolved?.let { clipboard.update(it) }
                }
            },
            separator(),
            item(messages["menu.station.add"], KeyCodes.add) {
                action {
                    Modal.AddNewStation.open()
                }
            }
    )
}
