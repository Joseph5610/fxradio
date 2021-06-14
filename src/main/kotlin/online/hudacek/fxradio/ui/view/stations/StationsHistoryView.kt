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
import online.hudacek.fxradio.api.stations.model.tagsSplit
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.HistoryViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class StationsHistoryView : BaseView() {

    private val historyViewModel: HistoryViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()


    override val root = listview(historyViewModel.stationsProperty) {
        id = "stationsHistoryList"
        cellFormat {
            graphic = hbox(10) {
                alignment = Pos.CENTER_LEFT
                imageview {
                    it.stationImage(this)
                    effect = DropShadow(30.0, Color.LIGHTGRAY)
                    fitHeight = 30.0
                    fitWidth = 30.0
                }
                vbox {
                    label(it.name)
                    smallLabel(it.tagsSplit)
                }
            }
            onUserSelect(1) {
                playerViewModel.stationProperty.value = it
            }
            addClass(Styles.historyListItem)
        }

        showWhen {
            //Show only while Search results are shown
            libraryViewModel.stateProperty.booleanBinding {
                it is LibraryState.History
            }
        }
        addClass(Styles.historyListView)
    }
}