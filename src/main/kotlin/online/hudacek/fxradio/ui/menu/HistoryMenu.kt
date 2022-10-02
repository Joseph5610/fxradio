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

import online.hudacek.fxradio.ui.formatted
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.viewmodel.HistoryViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.action
import tornadofx.bind
import tornadofx.confirm
import tornadofx.disableWhen
import tornadofx.get
import tornadofx.imageview
import tornadofx.item

class HistoryMenu : BaseMenu("menu.history") {

    private val libraryViewModel: LibraryViewModel by inject()
    private val historyViewModel: HistoryViewModel by inject()
    private val selectedStationViewModel: SelectedStationViewModel by inject()

    private val showHistoryItem by lazy {
        item(messages["menu.history.show"], KeyCodes.history) {
            action {
                libraryViewModel.stateProperty.value = LibraryState.History
            }
        }
    }

    private val recentHistoryItem by lazy {
        menu(messages["menu.history.recent"]) {
            disableWhen {
                historyViewModel.stationsProperty.emptyProperty()
            }
            items.bind(historyViewModel.stationsProperty) {
                item(it.name) {
                    //for some reason macos native menu does not respect
                    //width/height setting so it is disabled for now
                    if (!appMenuViewModel.usePlatformProperty.value) {
                        graphic = imageview {
                            it.stationImage(this)
                            fitHeight = 15.0
                            fitWidth = 15.0
                        }
                    }
                    action {
                        selectedStationViewModel.stationProperty.value = it
                    }
                }
            }
        }
    }

    private val clearHistoryItem by lazy {
        item(messages["menu.history.clear"]) {
            disableWhen {
                historyViewModel.stationsProperty.emptyProperty()
            }

            action {
                confirm(messages["history.clear.confirm"],
                        messages["history.clear.text"].formatted(historyViewModel.stationsProperty.size),
                        owner = primaryStage) {
                    historyViewModel.cleanupHistory()
                }
            }
        }
    }

    override val menuItems = listOf(showHistoryItem, recentHistoryItem, separator(), clearHistoryItem)
}
