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

package online.hudacek.fxradio.views.stations

import com.github.thomasnield.rxkotlinfx.onChangedObservable
import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import mu.KotlinLogging
import online.hudacek.fxradio.viewmodel.LibraryType
import online.hudacek.fxradio.fragments.StationInfoFragment
import online.hudacek.fxradio.utils.createImage
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.showPopover

/**
 * Main view of stations
 * DataGrid shows radio station logo and name
 */
class StationsDataGridView : View() {
    private val logger = KotlinLogging.logger {}

    private val playerViewModel: PlayerViewModel by inject()
    private val stationsViewModel: StationsViewModel by inject()
    private val stationsHistoryView: StationsHistoryViewModel by inject()

    private val libraryViewModel: LibraryViewModel by inject()

    init {
        stationsViewModel.item = StationsModel()

        libraryViewModel.selectedProperty.onChange {
            logger.debug { "selectedProperty changed: $it" }
            it?.let(::showLibraryType)
        }
    }

    override fun onDock() {
        //Default View
        showLibraryType(libraryViewModel.selectedProperty.value)
    }

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

        cellCache {
            vbox(alignment = Pos.CENTER) {
                onRightClick {
                    popover {
                        title = it.name
                        isCloseButtonEnabled = true
                        isHeaderAlwaysVisible = true
                        vbox {
                            add(StationInfoFragment(it))
                        }
                    }
                    showPopover()
                }

                onHover { _ -> tooltip(it.name) }

                paddingAll = 5
                vbox(alignment = Pos.CENTER) {
                    prefHeight = 120.0
                    paddingAll = 5
                    imageview {
                        createImage(it)
                        effect = DropShadow(15.0, Color.LIGHTGRAY)
                        isCache = true
                        cacheHint = CacheHint.SPEED
                        fitHeight = 100.0
                        fitWidth = 100.0
                        isPreserveRatio = true
                    }
                }
                label(it.name) {
                    style {
                        fontSize = 14.px
                    }
                }
            }
        }

        showWhen {
            stationsViewModel.stationsViewStateProperty.isEqualTo(StationsViewState.Normal)
        }
    }

    private fun showLibraryType(selected: SelectedLibrary) {
        stationsViewModel.stationsViewStateProperty.value = StationsViewState.Loading
        with(selected) {
            when (type) {
                LibraryType.Country -> stationsViewModel.stationsByCountry(params)
                LibraryType.Favourites -> stationsViewModel.favourites
                LibraryType.History -> stationsViewModel.show(stationsHistoryView.stationsProperty)
                LibraryType.Search -> stationsViewModel.search(params)
                else -> stationsViewModel.topStations
            }
        }
    }
}
