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
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.ui.modal.StationDebugFragment
import online.hudacek.fxradio.ui.modal.StationInfoFragment
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.ui.view.menu.FavouritesMenu
import online.hudacek.fxradio.ui.viewmodel.*
import tornadofx.*

/**
 * Main view of stations
 * DataGrid shows radio station logo and name
 */
class StationsDataGridView : View() {
    private val events: AppEvent by inject()

    private val playerViewModel: PlayerViewModel by inject()
    private val stationsViewModel: StationsViewModel by inject()
    private val favouritesMenu: FavouritesMenu by inject()
    private val selectedLibraryViewModel: SelectedLibraryViewModel by inject()

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
                    items.addAll(favouritesMenu.addRemoveItems)

                    separator()
                    item(messages["menu.station.vote"]) {
                        actionEvents()
                                .map { station }
                                .subscribe(events.vote)
                    }

                    if (Config.Flags.enableStationDebug) {
                        separator()
                        item("Station Debug Info").action {
                            find<StationDebugFragment>().openModal(stageStyle = StageStyle.UTILITY)
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
                    onHover { _ -> tooltip(station.name) }
                    style {
                        fontSize = 13.px
                    }
                }
                smallLabel(createStationTags(station.tags, station.country))
            }
        }

        showWhen {
            stationsViewModel.viewStateProperty.isEqualTo(StationsViewState.Loaded)
                    .and(selectedLibraryViewModel.itemProperty.booleanBinding {
                        it?.type != LibraryType.History
                    })
        }
    }

    //Small label shown under the station name in the grid
    //Contains tag or country name of station
    private fun createStationTags(tags: String, country: String): String {
        val stationTagsSplit = tags.split(",")
        return when {
            tags.isEmpty() -> country
            stationTagsSplit.size > 1 -> stationTagsSplit[0].capitalize() + ", " + stationTagsSplit[1].capitalize()
            else -> stationTagsSplit[0].capitalize()
        }
    }
}
