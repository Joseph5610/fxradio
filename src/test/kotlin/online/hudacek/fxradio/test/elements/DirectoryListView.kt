package online.hudacek.fxradio.test.elements

import javafx.scene.control.Label
import javafx.scene.control.ListView
import online.hudacek.fxradio.test.util.find
import online.hudacek.fxradio.test.util.waitFor
import org.testfx.api.FxRobot

class DirectoryListView(private val robot: FxRobot) {

    private val libraryDirectoryView = "#libraryDirectoryView"

    fun waitForElement(): ListView<String> {
        val browseDirectory = robot.find(libraryDirectoryView) as ListView<String>
        waitFor(2) { browseDirectory.items.size == 1 }
        return browseDirectory
    }

    fun openBrowseAllCountries(browseDirectory: ListView<String>) = apply {
        robot.interact {
            val directoryItem = robot.from(browseDirectory)
                .lookup(browseDirectory.items[0])
                .query<Label>()
            robot.clickOn(directoryItem)
        }
    }
}
