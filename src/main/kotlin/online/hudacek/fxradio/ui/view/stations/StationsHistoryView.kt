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

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.HistoryViewModel
import online.hudacek.fxradio.ui.viewmodel.LibraryType
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import online.hudacek.fxradio.ui.viewmodel.SelectedLibraryViewModel
import tornadofx.*

class StationsHistoryView : View() {

    private val historyViewModel: HistoryViewModel by inject()
    private val selectedLibraryViewModel: SelectedLibraryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    override val root = listview(historyViewModel.stationsProperty) {

        cellFormat {
            graphic = hbox(10) {
                alignment = Pos.CENTER_LEFT
                imageview {
                    it.stationImage(this)
                    effect = DropShadow(30.0, Color.LIGHTGRAY)
                    fitHeight = 30.0
                    fitWidth = 30.0
                    isPreserveRatio = true
                }
                label(it.name) {
                }
            }
            onUserSelect(1) {
                playerViewModel.stationProperty.value = it
            }

            addClass(Styles.historyListItem)
        }

        showWhen {
            //Show only while Search results are shown
            selectedLibraryViewModel.itemProperty.booleanBinding {
                it?.type == LibraryType.History
            }
        }
        addClass(Styles.historyListView)
    }
}