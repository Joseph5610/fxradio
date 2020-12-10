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

import com.github.thomasnield.rxkotlinfx.onChangedObservable
import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import mu.KotlinLogging
import online.hudacek.fxradio.ui.fragment.StationInfoFragment
import online.hudacek.fxradio.ui.viewmodel.*
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.utils.smallLabel
import online.hudacek.fxradio.utils.stationImage
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
    private val historyViewModel: HistoryViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()

    init {
        stationsViewModel.item = StationsModel()

        libraryViewModel.selectedProperty.onChange {
            logger.debug { "selectedProperty changed: $it" }
            it?.let(::showLibrary)
        }

        //Refresh search on query change
        libraryViewModel.searchQueryProperty.onChange {
            with(libraryViewModel.selectedProperty.value) {
                if (type == LibraryType.Search)
                    stationsViewModel.search(libraryViewModel.searchQueryProperty.value)
                else if (type == LibraryType.SearchByTag)
                    stationsViewModel.searchByTag(libraryViewModel.searchQueryProperty.value)
            }
        }
    }

    override fun onDock() {
        //Default View
        showLibrary(libraryViewModel.selectedProperty.value)
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
            vbox {
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
                        it.stationImage(this)
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
                        fontSize = 13.px
                    }
                }

                val stationTagsSplit = it.tags.split(",")
                val tagsLabel = when {
                    it.tags.isEmpty() -> it.country
                    stationTagsSplit.size > 1 -> stationTagsSplit[0].capitalize() + ", " + stationTagsSplit[1].capitalize()
                    else -> stationTagsSplit[0].capitalize()
                }

                smallLabel(tagsLabel)
            }
        }

        showWhen {
            stationsViewModel.viewStateProperty.isEqualTo(StationsViewState.Normal)
        }
    }

    private fun showLibrary(selected: SelectedLibrary) {
        stationsViewModel.viewStateProperty.value = StationsViewState.Loading
        with(selected) {
            when (type) {
                LibraryType.Country -> stationsViewModel.stationsByCountry(params)
                LibraryType.Favourites -> stationsViewModel.show(favouritesViewModel.stationsProperty)
                LibraryType.History -> stationsViewModel.show(historyViewModel.stationsProperty)
                LibraryType.TopStations -> stationsViewModel.topStations
                LibraryType.Search -> stationsViewModel.search(libraryViewModel.searchQueryProperty.value)
                LibraryType.SearchByTag -> stationsViewModel.searchByTag(libraryViewModel.searchQueryProperty.value)
            }
        }
    }
}
