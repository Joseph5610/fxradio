package online.hudacek.fxradio.test.elements

import javafx.scene.control.Label
import javafx.scene.control.ListView
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.test.util.findAll
import online.hudacek.fxradio.test.util.waitFor
import org.testfx.api.FxRobot

class PinnedCountries(private val robot: FxRobot) {

    private val libraryCountriesFragment = "#libraryCountriesFragment"

    fun verifyPinnedCountryExists(): Label = robot.from(getListViewElement()[0])
        .lookup("TestPinnedCountryName")
        .query()

    fun verifyPinnedCountryDoesNotExist() {
        val items = robot.from(getListViewElement()[0])
            .lookup("TestPinnedCountryName")
            .queryAll<Label>()
        waitFor(2) { items.size == 0 }
    }

    private fun getListViewElement() = robot.findAll<ListView<Country>>(libraryCountriesFragment).toList()
}
