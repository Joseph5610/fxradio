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

import javafx.beans.property.SimpleListProperty
import javafx.geometry.Pos
import online.hudacek.fxradio.apiclient.stations.model.Station
import online.hudacek.fxradio.apiclient.stations.model.tagsSplit
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.HistoryViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.StationInfo
import online.hudacek.fxradio.viewmodel.StationInfoViewModel
import tornadofx.addClass
import tornadofx.bindChildren
import tornadofx.bindSelected
import tornadofx.booleanBinding
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.listview
import tornadofx.onUserSelect
import tornadofx.vbox

private const val LOGO_SIZE = 30.0

class StationsHistoryView : BaseView() {

    private val historyViewModel: HistoryViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()
    private val stationInfoViewModel: StationInfoViewModel by inject()

    override val root = listview<Station>(historyViewModel.stationsProperty) {

        //Cleanup selected item on refresh of library
        playerViewModel.stationObservable.subscribe {
            selectionModel.clearSelection()
            selectionModel.select(playerViewModel.stationProperty.value)
        }

        id = "stationsHistoryList"
        cellFormat {
            graphic = hbox(spacing = 10) {
                alignment = Pos.CENTER_LEFT
                imageview {
                    it.stationImage(this)
                    fitHeight = LOGO_SIZE
                    fitWidth = LOGO_SIZE
                }
                vbox {
                    label(it.name)
                    smallLabel(it.tagsSplit)
                }
            }
            onUserSelect(1) {
                playerViewModel.stationProperty.value = it
                stationInfoViewModel.item = StationInfo(it)
            }
            addClass(Styles.historyListItem)
        }

        showWhen {
            // Show only while Library State is History
            libraryViewModel.stateProperty.booleanBinding {
                it is LibraryState.History
            }
        }
        addClass(Styles.historyListView)
    }
}
