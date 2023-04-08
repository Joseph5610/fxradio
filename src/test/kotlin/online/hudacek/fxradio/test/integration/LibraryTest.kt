/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.test.integration

import javafx.stage.Stage
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.test.elements.CountrySearch
import online.hudacek.fxradio.test.elements.CountryDirectory
import online.hudacek.fxradio.test.elements.PinnedCountries
import online.hudacek.fxradio.test.elements.Player
import online.hudacek.fxradio.test.elements.StationSearch
import online.hudacek.fxradio.test.util.sleep
import online.hudacek.fxradio.usecase.country.GetCountriesUseCase
import online.hudacek.fxradio.viewmodel.*
import org.junit.jupiter.api.*
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop
import tornadofx.find
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Basic functionality test
 * macOS: enable IntelliJ in Settings > Privacy > Accessibility to make it work
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("Library tests")
class LibraryTest : BaseTest() {

    @Start
    fun start(stage: Stage) = loadApp(stage)

    @Stop
    fun stop() = stopApp()

    @Test
    @Order(1)
    fun `search should show correct results`() {
        val stationSearch = StationSearch(robot)
        try {
            // Verify app initial state
            Player(robot).waitForStatusHasText("Streaming stopped")

            // Verify search field is present
            stationSearch
                .waitForElement()
                // Verify short query UI
                .enterSearchQuery("st")
                .verifyThatEmptySearchViewIsPresent()
                // Enter query into field
                .enterSearchQuery("station")

            // Wait until DataGrid is loaded with stations for the provided search query
            sleep(seconds = 8)
            stationSearch.verifyThatEmptySearchViewIsNotPresent()
        } finally {
            stationSearch.clearSearchQuery()
        }
    }

    @Test
    fun `pinned country should appear in pinned list`() {
        // Verify app initial state
        Player(robot).waitForStatusHasText("Streaming stopped")

        // Simulate add country to pinned list
        robot.interact {
            val testCountry = Country("TestPinnedCountryName", "AF", 250)
            val library = find<LibraryViewModel>()
            logger.info { "Pin country $testCountry" }
            library.pinCountry(testCountry)
        }

        val pinnedCountries = PinnedCountries(robot)

        // Find "TestPinnedCountryName" in the list of items
        val pinnedCountry = pinnedCountries.verifyPinnedCountryExists()

        // Simulate remove country to pinned list
        robot.interact {
            val library = find<LibraryViewModel>()
            logger.info { "Unpin item: $pinnedCountry" }
            library.unpinCountry(Country(pinnedCountry.text, "AF", 250))
        }

        // Check there is no item with this label in the app
        pinnedCountries.verifyPinnedCountryDoesNotExist()
    }

    @Test
    fun `verify directory button opens internal window`() {
        val countrySearch = CountrySearch(robot)
        try {
            // Verify app initial state
            Player(robot).waitForStatusHasText("Streaming stopped")

            CountryDirectory(robot)
                // Verify browse Directory item is present
                .waitForElement()
                // Open directory window
                .openBrowseAllCountries()

            val expectedCountriesSize = find<GetCountriesUseCase>().execute(Unit).count().blockingGet()

            // Verify Countries Window is open
            countrySearch.waitForElement()
                // Verify list of countries is correctly populated
                .verifyListViewHasSize(expectedSize = expectedCountriesSize.toInt())
        } finally {
            countrySearch.closeWindow()
        }
    }
}
