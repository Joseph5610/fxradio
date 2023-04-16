package online.hudacek.fxradio.test.elements

import javafx.scene.control.Label
import javafx.scene.control.ListView
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.test.util.find
import online.hudacek.fxradio.test.util.waitFor
import org.testfx.api.FxRobot

class PinnedCountries(private val robot: FxRobot) {

    private val libraryCountriesView = "#libraryCountriesView"

    private val listViewElement: ListView<Country>
        get() = robot.find(libraryCountriesView)

    fun verifyPinnedCountryExists(): Label = robot.from(listViewElement)
        .lookup("TestPinnedCountryName")
        .query()

    fun verifyPinnedCountryDoesNotExist() {
        val items = robot.from(listViewElement)
            .lookup("TestPinnedCountryName")
            .queryAll<Label>()
        waitFor(2) { items.size == 0 }
    }
}
