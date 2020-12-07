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

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.utils.menu
import online.hudacek.fxradio.utils.shouldBeDisabled
import online.hudacek.fxradio.utils.update
import online.hudacek.fxradio.viewmodel.MenuViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class StationMenu : Controller() {

    private val viewModel: MenuViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    private val keyInfo = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)
    private val keyAdd = KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)

    val menu by lazy {
        menu(messages["menu.station"]) {
            item(messages["menu.station.info"], keyInfo) {
                shouldBeDisabled(playerViewModel.stationProperty)
                action {
                    viewModel.openStationInfo()
                }
            }
            separator()
            item(messages["menu.station.vote"]) {
                shouldBeDisabled(playerViewModel.stationProperty)
                action {
                    playerViewModel.addVote()
                }
            }
            item(messages["copy.stream.url"]) {
                shouldBeDisabled(playerViewModel.stationProperty)
                action {
                    playerViewModel.stationProperty.value.url_resolved?.let { clipboard.update(it) }
                }
            }

            separator()
            item(messages["menu.station.add"], keyAdd) {
                action {
                    viewModel.openAddNewStation()
                }
            }
        }
    }
}
