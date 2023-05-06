package online.hudacek.fxradio.ui.util

import javafx.scene.input.KeyCode.LEFT
import javafx.scene.input.KeyCode.RIGHT
import javafx.scene.input.KeyCode.TAB
import javafx.scene.input.KeyEvent
import tornadofx.DataGrid

/**
 * Basic keyword navigation support for DataGrid
 */
class DataGridHandler<T>(private val dataGrid: DataGrid<T>) {

    fun handle(key: KeyEvent) {
        with(dataGrid.selectionModel) {
            if (key.code == TAB) {
                if (selectedIndex == -1) {
                    select(0)
                    key.consume()
                } else {
                    clearSelection()
                }
            }

            if (key.code == RIGHT) {
                selectNext()
                key.consume()
            }

            if (key.code == LEFT) {
                selectPrevious()
                key.consume()
            }
        }
    }
}
