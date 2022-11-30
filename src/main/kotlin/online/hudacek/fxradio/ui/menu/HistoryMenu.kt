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

import com.github.thomasnield.rxkotlinfx.actionEvents
import online.hudacek.fxradio.ui.util.formatted
import online.hudacek.fxradio.ui.util.stationView
import online.hudacek.fxradio.util.AlertHelper.confirmAlert
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*

class HistoryMenu : BaseMenu("menu.history") {

    private val libraryViewModel: LibraryViewModel by inject()
    private val historyViewModel: HistoryViewModel by inject()
    private val selectedStationViewModel: SelectedStationViewModel by inject()

    private val showHistoryItem by lazy {
        item(messages["menu.history.show"], KeyCodes.historyView) {
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
                    // For some reason macOS native menu does not respect
                    // width/height setting, so it is disabled for now
                    if (!appMenuViewModel.usePlatformProperty.value) {
                        graphic = stationView(it, 15.0)
                    }
                    action {
                        selectedStationViewModel.item = SelectedStation(it)
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

            actionEvents()
                .flatMapMaybe {
                    confirmAlert(
                        messages["history.clear.confirm"],
                        messages["history.clear.text"].formatted(historyViewModel.stationsProperty.size),
                    )
                }.subscribe { historyViewModel.cleanupHistory() }
        }
    }

    override val menuItems = listOf(showHistoryItem, recentHistoryItem, separator(), clearHistoryItem)
}
