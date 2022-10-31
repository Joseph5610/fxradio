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

import javafx.scene.layout.Priority
import javafx.util.Duration
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.History
import online.hudacek.fxradio.viewmodel.InfoPanelState
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.ViewTransition
import tornadofx.addClass
import tornadofx.booleanBinding
import tornadofx.borderpane
import tornadofx.center
import tornadofx.fitToParentHeight
import tornadofx.hgrow
import tornadofx.osgi.addViewsWhen
import tornadofx.paddingAll
import tornadofx.paddingTop
import tornadofx.region
import tornadofx.replaceWith
import tornadofx.right
import tornadofx.top
import tornadofx.vbox
import tornadofx.vgrow

/**
 * Main left pane of the app
 */
class StationsView : BaseView() {

    private val messageView: StationsEmptyView by inject()
    private val headerView: StationsHeaderView by inject()
    private val dataGridView: StationsDataGridView by inject()
    private val historyView: StationsHistoryView by inject()
    private val stationsInfoView: StationsInfoView by inject()

    private val selectedStationViewModel: SelectedStationViewModel by inject()

    override val root = borderpane {
        vgrow = Priority.ALWAYS

        top {
            add(headerView)
        }

        center {
            vbox(spacing = 5.0){
                paddingAll = 5.0
                hgrow = Priority.ALWAYS
                add(messageView)
                add(dataGridView)
                add(historyView)
            }
        }

        right {
            add(stationsInfoView)
            right.showWhen {
                selectedStationViewModel.stationProperty.booleanBinding {
                    it?.isValid() == true
                }.and(selectedStationViewModel.stateProperty.booleanBinding {
                    it is InfoPanelState.Shown
                })
            }
        }

        dataGridView.root.fitToParentHeight()
        historyView.root.fitToParentHeight()
        addClass(Styles.backgroundWhite)
    }
}
