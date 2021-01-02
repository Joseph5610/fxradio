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
import online.hudacek.fxradio.storage.stationImage
import online.hudacek.fxradio.ui.fragment.StationInfoFragment
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import online.hudacek.fxradio.ui.viewmodel.StationsViewModel
import online.hudacek.fxradio.ui.viewmodel.StationsViewState
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.utils.smallLabel
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.showPopover

/**
 * Main view of stations
 * DataGrid shows radio station logo and name
 */
class StationsDataGridView : View() {

    private val playerViewModel: PlayerViewModel by inject()
    private val stationsViewModel: StationsViewModel by inject()

    override fun onDock() {
        //Load the stations grid
        stationsViewModel.viewStateProperty.value = StationsViewState.Loading
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
                smallLabel(createStationTags(it.tags, it.country))
            }
        }

        showWhen {
            stationsViewModel.viewStateProperty.isEqualTo(StationsViewState.Normal)
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
