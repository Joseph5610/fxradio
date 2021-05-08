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

package online.hudacek.fxradio.ui.menu

import online.hudacek.fxradio.ui.modal.Modals
import online.hudacek.fxradio.ui.modal.open
import online.hudacek.fxradio.ui.update
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.action
import tornadofx.get

class StationMenu : BaseMenu("menu.station") {

    private val playerViewModel: PlayerViewModel by inject()

    override val menuItems = listOf(
            item(messages["menu.station.info"], KeyCodes.info) {
                disableWhenInvalidStation(playerViewModel.stationProperty)
                action {
                    Modals.StationInfo.open()
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
                    Modals.AddNewStation.open()
                }
            }
    )
}
