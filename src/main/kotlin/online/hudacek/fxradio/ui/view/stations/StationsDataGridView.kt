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

import javafx.geometry.Pos
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.util.Duration
import online.hudacek.fxradio.apiclient.radiobrowser.model.description
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.item
import online.hudacek.fxradio.ui.menu.platformContextMenu
import online.hudacek.fxradio.ui.util.DataCellHandler
import online.hudacek.fxradio.ui.util.DataGridHandler
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.ui.util.smallLabel
import online.hudacek.fxradio.ui.util.stationView
import online.hudacek.fxradio.util.toObservableChanges
import online.hudacek.fxradio.viewmodel.InfoPanelState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SelectedStation
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import online.hudacek.fxradio.viewmodel.StationsState
import online.hudacek.fxradio.viewmodel.StationsViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.action
import tornadofx.booleanBinding
import tornadofx.datagrid
import tornadofx.get
import tornadofx.label
import tornadofx.onHover
import tornadofx.onLeftClick
import tornadofx.paddingAll
import tornadofx.paddingTop
import tornadofx.point
import tornadofx.px
import tornadofx.scale
import tornadofx.style
import tornadofx.tooltip
import tornadofx.vbox

private const val CELL_WIDTH = 140.0
private const val LOGO_SIZE = 100.0

/**
 * Main view of stations
 * DataGrid shows radio station logo and name
 */
class StationsDataGridView : BaseView() {

    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val stationsViewModel: StationsViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    override val root = datagrid(stationsViewModel.stationsProperty) {
        id = "stations"

        val handler = DataGridHandler(this)
        setOnKeyPressed(handler::handle)

        cellWidth = CELL_WIDTH

        // Cleanup selected item on refresh of library
        itemsProperty.toObservableChanges().subscribe {
            selectionModel.clearSelection()
            selectionModel.select(selectedStationViewModel.stationProperty.value)
        }

        cellFormat {
            val cellHandler = DataCellHandler(this, this@datagrid)

            onHover {
                if (it) {
                    scale(Duration.seconds(0.07), point(1.05, 1.05))
                } else {
                    scale(Duration.seconds(0.07), point(1.0, 1.0))
                }
            }

            // Workaround for https://github.com/edvin/tornadofx/issues/1216
            onLeftClick {
                selectionModel.select(it)
                if (selectedStationViewModel.item.station != it) {
                    selectedStationViewModel.item = SelectedStation(it)
                }
            }

            addEventFilter(MouseEvent.DRAG_DETECTED, cellHandler::onDragDetected)
            addEventFilter(DragEvent.DRAG_OVER, cellHandler::onDragOver)
            addEventFilter(DragEvent.DRAG_ENTERED, cellHandler::onDragEntered)
            addEventFilter(DragEvent.DRAG_EXITED, cellHandler::onDragExited)
            addEventFilter(DragEvent.DRAG_DROPPED, cellHandler::onDragDropped)
            addEventFilter(DragEvent.DRAG_DONE, cellHandler::onDragDone)
        }

        cellCache { station ->
            vbox(alignment = Pos.BOTTOM_CENTER) {

                onHover {
                    tooltip(station.name)
                }

                platformContextMenu(listOf(item(messages["menu.station.info"]) {
                    action {
                        selectedStationViewModel.stateProperty.value = InfoPanelState.Shown
                    }
                }))

                stationView(station, LOGO_SIZE) {
                    paddingAll = 5
                }

                label(station.name) {
                    if (station.hasExtendedInfo) {
                        graphic = FontAwesome.Glyph.CHECK_CIRCLE.make(size = 13.0, isPrimary = true)
                    }
                    paddingTop = 5
                    style {
                        fontSize = 13.px
                    }
                }
                smallLabel(station.description)
            }
        }

        showWhen {
            stationsViewModel.stateProperty.booleanBinding {
                when (it) {
                    is StationsState.Fetched -> true
                    else -> false
                }
            }
        }
    }
}
