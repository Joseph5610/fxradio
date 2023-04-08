package online.hudacek.fxradio.test.elements

import javafx.scene.control.ListView
import javafx.scene.input.KeyCode
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.test.util.find
import online.hudacek.fxradio.test.util.visible
import online.hudacek.fxradio.test.util.waitFor
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot

class CountriesSearchFragment(private val robot: FxRobot) {

    private val countriesSearchFragment = "#countriesSearchFragment"

    fun waitForElement() = apply {
        verifyThat(countriesSearchFragment, visible())
    }

    fun verifyListViewHasSize(expectedSize: Int) = apply {
        val listView = robot.find(countriesSearchFragment) as ListView<Country>
        waitFor(2) { listView.items.size == expectedSize }
    }

    fun closeWindow() = apply {
        robot.press(KeyCode.ESCAPE)
    }
}
