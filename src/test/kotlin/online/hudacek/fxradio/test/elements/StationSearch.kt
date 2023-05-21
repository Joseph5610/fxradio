package online.hudacek.fxradio.test.elements

import online.hudacek.fxradio.test.util.clearField
import online.hudacek.fxradio.test.util.enterText
import online.hudacek.fxradio.test.util.hasText
import online.hudacek.fxradio.test.util.hasValue
import online.hudacek.fxradio.test.util.hidden
import online.hudacek.fxradio.test.util.sleep
import online.hudacek.fxradio.test.util.visible
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot

class StationSearch(private val robot: FxRobot) {

    private val search = "#search"
    private val stationMessageHeader = "#stationMessageHeader"
    private val stationMessageSubHeader = "#stationMessageSubHeader"
    private val stationsEmptyView = "#stationsEmptyView"

    fun waitForElement() = apply {
        verifyThat(search, visible())
    }

    fun enterSearchQuery(query: String) = apply {
        robot.enterText(search, query)
        verifyThat(search, hasValue(query))
        sleep(seconds = 2) // test was too quick sometimes
    }

    fun clearSearchQuery() = apply {
        robot.clearField(search)
    }

    fun verifyThatEmptySearchViewIsPresent() = apply {
        verifyThat(stationsEmptyView, visible())
        verifyThat(stationMessageHeader, visible())
        verifyThat(stationMessageSubHeader, visible())
        verifyThat(stationMessageHeader, hasText("Searching Radio Directory"))
    }

    fun verifyThatEmptySearchViewIsNotPresent() = apply {
        verifyThat(stationsEmptyView, hidden())
    }
}
