package online.hudacek.fxradio.ui.util

import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.util.Duration
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.viewmodel.FavouritesViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.*

private val dfStation = DataFormat("station")

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
            dataCell.startDragAndDrop(TransferMode.MOVE).apply {
                put(dfStation, dataCell.item)
            }
            e.consume()
        }
    }

    fun onDragOver(e: DragEvent) {
        if (libraryViewModel.stateProperty.value is LibraryState.Favourites) {
            if (e.gestureSource != this && e.dragboard.hasContent(dfStation)) {
                e.acceptTransferModes(TransferMode.MOVE)
            }
            e.consume()
        }
    }

    fun onDragEntered(e: DragEvent) {
        if (libraryViewModel.stateProperty.value is LibraryState.Favourites) {
            if (e.gestureSource != this && e.dragboard.hasContent(dfStation)) {
                dataCell.scale(Duration.seconds(0.05), point(0.9, 0.9))
            }
            e.consume()
        }
    }

    fun onDragExited(e: DragEvent) {
        dataCell.scale(Duration.seconds(0.05), point(1.0, 1.0))
        e.consume()
    }

    fun onDragDropped(e: DragEvent) {
        if (libraryViewModel.stateProperty.value is LibraryState.Favourites) {
            with(e.dragboard) {
                e.isDropCompleted = if (hasContent(dfStation)) {
                    val dropped = getContent(dfStation) as Station
                    dataGrid.items.swap(dropped, dataCell.item)
                    true
                } else {
                    false
                }
                e.consume()
            }
        }
    }

    fun onDragDone(e: DragEvent) {
        if (e.transferMode == TransferMode.MOVE) {
            favouritesViewModel.commit()
        }
        e.consume()
    }
}