package online.hudacek.fxradio.ui.util

import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.util.Duration
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.viewmodel.FavouritesViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.Component
import tornadofx.DataGrid
import tornadofx.DataGridCell
import tornadofx.move
import tornadofx.point
import tornadofx.put
import tornadofx.scale

private val dfStation = DataFormat("station")

private val logger = KotlinLogging.logger {}

/**
 * Basic drag and drop support for DataGrid
 */
class DataCellHandler(
    private val dataCell: DataGridCell<Station>,
    private val dataGrid: DataGrid<Station>
) : Component() {

    private val libraryViewModel: LibraryViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()

    fun onDragDetected(e: MouseEvent) {
        if (libraryViewModel.stateProperty.value is LibraryState.Favourites) {
            if (!dataCell.updating) {
                logger.trace { "OnDragDetected for ${dataCell.item.uuid}" }
                dataCell.startDragAndDrop(TransferMode.MOVE).apply {
                    put(dfStation, dataCell.item)
                }
            }
            e.consume()
        }
    }

    fun onDragOver(e: DragEvent) {
        if (libraryViewModel.stateProperty.value is LibraryState.Favourites) {
            if (e.gestureSource != this && e.dragboard.hasContent(dfStation)) {
                logger.trace { "OnDragOver for ${dataCell.item.uuid}" }
                e.acceptTransferModes(TransferMode.MOVE)
            }
            e.consume()
        }
    }

    fun onDragEntered(e: DragEvent) {
        if (libraryViewModel.stateProperty.value is LibraryState.Favourites) {
            if (e.gestureSource != this && e.dragboard.hasContent(dfStation)) {
                logger.trace { "OnDragEntered for ${dataCell.item.uuid}" }
                if (dataCell.scaleX == 1.0) {
                    dataCell.scale(Duration.seconds(0.05), point(0.9, 0.9))
                }
            }
            e.consume()
        }
    }

    fun onDragExited(e: DragEvent) {
        logger.trace { "OnDragExited for ${dataCell.item.uuid}" }
        dataCell.scale(Duration.seconds(0.05), point(1.0, 1.0))
        e.consume()
    }

    fun onDragDropped(e: DragEvent) {
        if (libraryViewModel.stateProperty.value is LibraryState.Favourites) {
            with(e.dragboard) {
                try {
                    if (hasContent(dfStation)) {
                        logger.trace { "OnDragDropped for ${dataCell.item.uuid}" }
                        val droppedItem = getContent(dfStation) as Station

                        val targetIndex = dataGrid.items.indexOf(dataCell.item)
                        if (targetIndex != -1) {
                            dataGrid.items.move(droppedItem, targetIndex)
                            clear()
                        }
                    }
                } finally {
                    e.isDropCompleted = true
                }
            }
            e.consume()
        }
    }

    fun onDragDone(e: DragEvent) {
        if (e.transferMode == TransferMode.MOVE) {
            logger.trace { "OnDragDone for ${dataCell.item.uuid}" }
            favouritesViewModel.commit()
        }
        e.consume()
    }
}
