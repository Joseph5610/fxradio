package online.hudacek.fxradio.test.elements

import javafx.scene.control.Label
import javafx.scene.control.ListView
import online.hudacek.fxradio.test.util.find
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.matcher.control.ListViewMatchers

class CountryDirectory(private val robot: FxRobot) {

    private val libraryDirectoryView = "#libraryDirectoryView"

    fun waitForElement() = apply {
        verifyThat(libraryDirectoryView, ListViewMatchers.hasItems(1))
    }

    fun openBrowseAllCountries() = apply {
        val browseDirectory = robot.find<ListView<String>>(libraryDirectoryView)
        robot.interact {
            val directoryItem = robot.from(browseDirectory)
                .lookup(browseDirectory.items[0])
                .query<Label>()
            robot.clickOn(directoryItem)
        }
    }
}
