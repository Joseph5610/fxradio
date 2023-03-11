package online.hudacek.fxradio.ui.util

import javafx.scene.control.ListView
import javafx.scene.input.KeyCode.TAB
import javafx.scene.input.KeyEvent

class ListViewHandler<T>(private val listView: ListView<T>) {

    fun handle(key: KeyEvent) {
        if (key.code == TAB) {
            with(listView.selectionModel) {
                if (selectedIndex == -1) {
                    listView.scrollTo(0)
                    select(0)
                    key.consume()
                } else {
                    clearSelection()
                    key.consume()
                }
            }
        }
    }
}