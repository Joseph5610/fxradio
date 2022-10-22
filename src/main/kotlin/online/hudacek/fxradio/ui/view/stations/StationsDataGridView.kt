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

import com.github.thomasnield.rxkotlinfx.toObservableChanges
import javafx.geometry.Pos
import javafx.util.Duration
import online.hudacek.fxradio.apiclient.radiobrowser.model.tagsSplit
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.FavouritesMenu
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.stationView
import online.hudacek.fxradio.ui.util.DataGridHandler
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*

private const val CELL_WIDTH = 140.0
private const val LOGO_SIZE = 100.0

/**
 * Main view of stations
 * DataGrid shows radio station logo and name
 */
class StationsDataGridView : BaseView() {

    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val stationsViewModel: StationsViewModel by inject()
    private val favouritesMenu: FavouritesMenu by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    //Show initial stations
    override fun onDock() = stationsViewModel.handleNewLibraryState(libraryViewModel.stateProperty.value)


    override val root = datagrid(stationsViewModel.stationsProperty) {
        id = "stations"

        val handler = DataGridHandler(this)
        setOnKeyPressed(handler::handle)

        cellWidth = CELL_WIDTH

        // Cleanup selected item on refresh of library
        itemsProperty.toObservableChanges().subscribe {
            selectionModel.clearSelection()
            selectionModel.select(selectedStationViewModel.stationProperty.value)
        }

        onUserSelect(1) {
            if (selectedStationViewModel.item.station != it) {
                selectedStationViewModel.item = SelectedStation(it)
            }
        }

        cellFormat {
            onHover {
                if (it) {
                    scale(Duration.seconds(0.07), point(1.05, 1.05), play = false).playFromStart()
                } else {
                    scale(Duration.seconds(0.07), point(1.0, 1.0), play = false).playFromStart()
                }
            }
        }

        cellCache { station ->
            vbox {
                paddingAll = 5

                onHover {
                    tooltip(station.name)
                }
                contextmenu {
                    // Add Add or Remove from favourites menu items
                    items.addAll(favouritesMenu.addRemoveFavouriteItems)
                    separator()
                    item(messages["menu.station.info"]).action {
                        selectedStationViewModel.stateProperty.value = InfoPanelState.Shown
                    }
                }

                vbox(alignment = Pos.CENTER) {
                    paddingAll = 5
                    prefHeight = 120.0
                    stationView(station) {
                        fitHeight = LOGO_SIZE
                        fitWidth = LOGO_SIZE
                    }
                }
                vbox(alignment = Pos.CENTER) {
                    label(station.name) {
                        style {
                            fontSize = 13.px
                        }
                    }
                    smallLabel(station.tagsSplit)
                }
            }
        }

        showWhen {
            stationsViewModel.stateProperty.booleanBinding {
                when (it) {
                    is StationsState.Fetched -> true
                    else -> false
                }
            }.and(libraryViewModel.stateProperty.booleanBinding {
                it !is LibraryState.History
            })
        }
    }
}
