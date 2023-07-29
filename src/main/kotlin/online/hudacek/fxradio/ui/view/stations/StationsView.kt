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

import javafx.geometry.Side
import javafx.scene.layout.Priority
import javafx.util.Duration.millis
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.viewmodel.InfoPanelState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import online.hudacek.fxradio.viewmodel.StationsState
import online.hudacek.fxradio.viewmodel.StationsViewModel
import tornadofx.addClass
import tornadofx.bind
import tornadofx.booleanBinding
import tornadofx.controlsfx.hiddensidepane
import tornadofx.controlsfx.right
import tornadofx.fitToParentHeight
import tornadofx.hgrow
import tornadofx.listProperty
import tornadofx.objectBinding
import tornadofx.observableListOf
import tornadofx.paddingAll
import tornadofx.vbox
import tornadofx.vgrow

/**
 * Main left pane of the app
 */
class StationsView : BaseView() {

    private val stationsEmptyView: StationsEmptyView by inject()
    private val headerView: StationsHeaderView by inject()
    private val dataGridView: StationsDataGridView by inject()
    private val stationsInfoView: StationsInfoView by inject()

    private val selectedStationViewModel: SelectedStationViewModel by inject()

    override val root = hiddensidepane {
        vgrow = Priority.ALWAYS
        triggerDistance = 0.0
        animationDuration = millis(170.0)

        content = vbox {
            vgrow = Priority.ALWAYS
            add(headerView)
            vbox(spacing = 5.0) {
                paddingAll = 5.0
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
                add(stationsEmptyView)
                add(dataGridView)
            }
        }

        right {
            add(stationsInfoView)
            pinnedSideProperty().bind(
                selectedStationViewModel.stateProperty.objectBinding {
                    if (it is InfoPanelState.Shown) Side.RIGHT
                    else null
                }
            )
        }

        dataGridView.root.fitToParentHeight()
        addClass(Styles.backgroundWhite)
    }
}
