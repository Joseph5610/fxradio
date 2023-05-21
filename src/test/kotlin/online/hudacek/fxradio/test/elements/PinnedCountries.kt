package online.hudacek.fxradio.test.elements

import javafx.scene.control.Label
import javafx.scene.control.ListView
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.test.util.find
import online.hudacek.fxradio.test.util.visible
import online.hudacek.fxradio.test.util.waitFor
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import java.util.Locale

class PinnedCountries(private val robot: FxRobot) {

    private val libraryCountriesView = "#libraryCountriesView"
    private val pinnedCountry = Locale.of("", "AF")

    private val listViewElement: ListView<Country>
        get() = robot.find(libraryCountriesView)

    fun verifyPinnedCountryExists() = apply {
        val item = robot.from(listViewElement)
            .lookup(pinnedCountry.displayCountry)
            .query<Label>()
        verifyThat(item, visible())
    }

    fun verifyPinnedCountryDoesNotExist() = apply {
        val items = robot.from(listViewElement)
            .lookup(pinnedCountry.displayCountry)
            .queryAll<Label>()
        waitFor(2) { items.size == 0 }
    }
}
