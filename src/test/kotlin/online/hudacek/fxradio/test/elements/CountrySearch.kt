package online.hudacek.fxradio.test.elements

import javafx.scene.input.KeyCode
import online.hudacek.fxradio.test.util.visible
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.matcher.control.ListViewMatchers

class CountrySearch(private val robot: FxRobot) {

    private val countriesSearchFragment = "#countriesSearchFragment"

    fun waitForElement() = apply {
        verifyThat(countriesSearchFragment, visible())
    }

    fun verifyListViewHasSize(expectedSize: Int) = apply {
        verifyThat(countriesSearchFragment, ListViewMatchers.hasItems(expectedSize))
    }

    fun closeWindow() = apply {
        robot.press(KeyCode.ESCAPE)
    }
}
