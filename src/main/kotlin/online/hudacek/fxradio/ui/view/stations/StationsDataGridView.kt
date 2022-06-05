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

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.github.thomasnield.rxkotlinfx.onChangedObservable
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.stations.model.tagsSplit
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.FavouritesMenu
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.ui.view.player.TickerView
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.new
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*

private const val gridCellWidth = 140.0
private const val gridStationLogoSize = 100.0

/**
 * Main view of stations
 * DataGrid shows radio station logo and name
 */
class StationsDataGridView : BaseView() {

    private val playerViewModel: PlayerViewModel by inject()
    private val stationsViewModel: StationsViewModel by inject()
    private val favouritesMenu: FavouritesMenu by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    //Show initial stations
    override fun onDock() = stationsViewModel.handleNewLibraryState(libraryViewModel.stateProperty.value)

    override val root = datagrid(stationsViewModel.stationsProperty) {
        id = "stations"
        cellWidth = gridCellWidth

        //Cleanup selected item on refresh of library
        itemsProperty
                .onChangedObservable()
                .subscribe {
                    selectionModel.clearSelection()
                }

        onUserSelect(1) {
            playerViewModel.stationProperty.value = it
        }

        cellCache { station ->
            vbox {
                paddingAll = 5

                onHover { tooltip(station.name) }
                contextmenu {
                    item(messages["menu.station.info"]).action {
                        Modal.StationInfo.new()
                    }

                    separator()

                    // Add Add or Remove from favourites menu items
                    items.addAll(favouritesMenu.addRemoveFavouriteItems)

                    separator()
                    item(messages["menu.station.vote"]) {
                        actionEvents()
                                .map { station }
                                .subscribe(appEvent.addVote)
                    }
                }

                vbox(alignment = Pos.CENTER) {
                    paddingAll = 5
                    prefHeight = 120.0
                    imageview {
                        station.stationImage(this)
                        fitHeight = gridStationLogoSize
                        fitWidth = gridStationLogoSize
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
