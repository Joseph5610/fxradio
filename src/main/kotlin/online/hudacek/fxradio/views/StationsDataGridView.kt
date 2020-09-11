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

package online.hudacek.fxradio.views

import com.github.thomasnield.rxkotlinfx.onChangedObservable
import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.fxradio.extension.createImage
import online.hudacek.fxradio.fragments.StationInfoFragment
import online.hudacek.fxradio.viewmodel.PlayerModel
import online.hudacek.fxradio.viewmodel.StationsModel
import online.hudacek.fxradio.viewmodel.StationsViewModel
import online.hudacek.fxradio.viewmodel.StationsViewState
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.showPopover

/**
 * Main view of stations
 * Datagrid shows radio station logo and name
 */
class StationsDataGridView : View() {

    private val playerModel: PlayerModel by inject()
    private val viewModel: StationsViewModel by inject()

    init {
        viewModel.item = StationsModel()
    }

    override val root = datagrid(viewModel.stationsProperty) {
        id = "stations"

        itemsProperty
                .onChangedObservable()
                .subscribe {
                    selectionModel.clearSelection()
                }

        onUserSelect(1) {
            playerModel.stationProperty.value = it
        }

        cellCache {
            vbox(alignment = Pos.CENTER) {
                popover {
                    title = it.name
                    isCloseButtonEnabled = true
                    isHeaderAlwaysVisible = true
                    vbox {
                        add(StationInfoFragment(it))
                    }
                }

                onRightClick { showPopover() }
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
        hiddenWhen(viewModel.isError.or(booleanBinding(viewModel.stationViewStatus) {
            when (this.value) {
                StationsViewState.Normal -> false
                else -> true
            }
        }))
    }
}
