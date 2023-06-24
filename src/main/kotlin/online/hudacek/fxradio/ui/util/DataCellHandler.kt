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

    /**
     * When drag is detected on [DataGridCell], puts the cell item in the DragBoard
     */
    fun onDragDetected(e: MouseEvent) {
        if (!isFavouritesOpen()) return
        if (!dataCell.updating) {
            dataCell
                .startDragAndDrop(TransferMode.MOVE)
                .put(dfStation, dataCell.item)
        }
        e.consume()
    }

    /**
     * Accept the dragged content in the potential drop target
     */
    fun onDragOver(e: DragEvent) {
        if (!isFavouritesOpen()) return
        if (e.gestureSource != this && e.dragboard.hasContent(dfStation)) {
            e.acceptTransferModes(TransferMode.MOVE)
        }
        e.consume()
    }

    /**
     * Start the scale animation on [DataGridCell] to indicate the drop is possible on this target
     */
    fun onDragEntered(e: DragEvent) {
        if (!isFavouritesOpen()) return
        if (e.gestureSource != this && e.dragboard.hasContent(dfStation)) {
            if (dataCell.scaleX == 1.0) {
                dataCell.scale(scaleDuration, scaleEnterPoint)
            }
        }
        e.consume()
    }

    /**
     * Return [DataGridCell] to the original scale
     */
    fun onDragExited(e: DragEvent) {
        logger.trace { "OnDragExited for ${dataCell.item.uuid}" }
        dataCell.scale(scaleDuration, scaleExitPoint)
        e.consume()
    }

    /**
     * Move the dragged item onto the position of the drop target
     */
    fun onDragDropped(e: DragEvent) {
        if (!isFavouritesOpen()) return

        with(e.dragboard) {
            try {
                if (hasContent(dfStation)) {
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

    /**
     * Save the new state of the [DataGrid] to the database
     */
    fun onDragDone(e: DragEvent) {
        if (e.transferMode == TransferMode.MOVE) {
            logger.trace { "OnDragDone for ${dataCell.item.uuid}" }
            favouritesViewModel.commit() // Save new state
        }
        e.consume()
    }

    /**
     * Drag and drop operation is only possible while Favourites library is opened
     */
    private fun isFavouritesOpen() = libraryViewModel.stateProperty.value == LibraryState.Favourites

    companion object {

        /**
         * Represents [Station] DataFormat
         */
        private val dfStation = DataFormat("application/x-station")

        /**
         * Scale In/Out duration
         */
        private val scaleDuration = Duration.seconds(0.05)

        /**
         * Scale Animation Enter Point
         */
        private val scaleEnterPoint = point(0.9, 0.9)

        /**
         * Scale Animation Exit Point
         */
        private val scaleExitPoint = point(1.0, 1.0)
    }
}
