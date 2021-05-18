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

package online.hudacek.fxradio.ui.view.stations

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.github.thomasnield.rxkotlinfx.onChangedObservable
import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.stations.model.tagsSplit
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.FavouritesMenu
import online.hudacek.fxradio.ui.modal.Modals
import online.hudacek.fxradio.ui.modal.StationInfoFragment
import online.hudacek.fxradio.ui.modal.open
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*

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
                contextmenu {
                    item(messages["menu.station.info"]).action {
                        StationInfoFragment(station).openModal(stageStyle = StageStyle.UTILITY)
                    }

                    separator()

                    //Add Add/Remove from favourites menu items
                    items.addAll(favouritesMenu.addRemoveFavouriteItems)

                    separator()
                    item(messages["menu.station.vote"]) {
                        actionEvents()
                                .map { station }
                                .subscribe(appEvent.addVote)
                    }

                    if (Config.Flags.enableStationDebug) {
                        separator()
                        item("Station Debug Info").action {
                            Modals.StationDebug.open()
                        }
                    }
                }

                paddingAll = 5
                vbox(alignment = Pos.CENTER) {
                    prefHeight = 120.0
                    paddingAll = 5
                    imageview {
                        station.stationImage(this)
                        effect = DropShadow(15.0, Color.LIGHTGRAY)
                        isCache = true
                        cacheHint = CacheHint.SPEED
                        fitHeight = 100.0
                        fitWidth = 100.0
                        isPreserveRatio = true
                    }
                }
                label(station.name) {
                    onHover { tooltip(station.name) }
                    style {
                        fontSize = 13.px
                    }
                }
                smallLabel(station.tagsSplit)
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
