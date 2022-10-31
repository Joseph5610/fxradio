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

package online.hudacek.fxradio.ui.view.stations

import javafx.geometry.Pos
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.apiclient.radiobrowser.model.tagsSplit
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.FavouritesMenu
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.stationView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.HistoryViewModel
import online.hudacek.fxradio.viewmodel.InfoPanelState
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SelectedStation
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.booleanBinding
import tornadofx.contextmenu
import tornadofx.get
import tornadofx.hbox
import tornadofx.item
import tornadofx.label
import tornadofx.listview
import tornadofx.onUserSelect
import tornadofx.separator
import tornadofx.vbox

private const val LOGO_SIZE = 30.0

class StationsHistoryView : BaseView() {

    private val historyViewModel: HistoryViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val favouritesMenu: FavouritesMenu by inject()

    override val root = listview<Station>(historyViewModel.stationsProperty) {
        id = "stationsHistoryList"

        // Cleanup selected item on refresh of library
        appEvent.historyUpdated.subscribe {
           selectionModel.select(0)
        }

        onUserSelect(1) {
            if (selectedStationViewModel.item.station != it) {
                selectedStationViewModel.item = SelectedStation(it)
            }
        }

        cellFormat {
            addClass(Styles.decoratedListItem)
        }

        cellCache {
            hbox(spacing = 10, alignment = Pos.CENTER_LEFT) {
                stationView(it) {
                    fitHeight = LOGO_SIZE
                    fitWidth = LOGO_SIZE
                }

                vbox {
                    label(it.name)
                    smallLabel(it.tagsSplit)
                }

                contextmenu {
                    // Add Add or Remove from favourites menu items
                    items.addAll(favouritesMenu.addRemoveFavouriteItems)
                    separator()
                    item(messages["menu.station.info"]).action {
                        selectedStationViewModel.stateProperty.value = InfoPanelState.Shown
                    }
                }
            }
        }

        showWhen {
            // Show only while Library State is History
            libraryViewModel.stateProperty.booleanBinding {
                it is LibraryState.History
            }
        }
        addClass(Styles.decoratedListView)
    }
}
