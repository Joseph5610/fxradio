package online.hudacek.fxradio.test.elements

import javafx.scene.control.Label
import javafx.scene.control.ListView
import online.hudacek.fxradio.test.util.find
import online.hudacek.fxradio.test.util.waitFor
import org.testfx.api.FxRobot

class DirectoryListView(private val robot: FxRobot) {

    private val libraryDirectoryView = "#libraryDirectoryView"

    fun waitForElement() = apply {
        waitFor(2) { getListViewElement().items.size == 1 }
    }

    fun openBrowseAllCountries() = apply {
        val browseDirectory = getListViewElement()
        robot.interact {
            val directoryItem = robot.from(browseDirectory)
                .lookup(browseDirectory.items[0])
                .query<Label>()
            robot.clickOn(directoryItem)
        }
    }

    private fun getListViewElement() = robot.find(libraryDirectoryView) as ListView<String>

}
