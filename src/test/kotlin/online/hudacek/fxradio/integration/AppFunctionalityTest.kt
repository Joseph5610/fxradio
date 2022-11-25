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

package online.hudacek.fxradio.integration

import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Slider
import javafx.stage.Stage
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.apiclient.ServiceProvider
import online.hudacek.fxradio.apiclient.radiobrowser.RadioBrowserApi
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.integration.Elements.libraryCountriesFragment
import online.hudacek.fxradio.integration.Elements.libraryListView
import online.hudacek.fxradio.integration.Elements.nowPlayingLabel
import online.hudacek.fxradio.integration.Elements.playerControls
import online.hudacek.fxradio.integration.Elements.search
import online.hudacek.fxradio.integration.Elements.stationMessageHeader
import online.hudacek.fxradio.integration.Elements.stationMessageSubHeader
import online.hudacek.fxradio.integration.Elements.stationsDataGrid
import online.hudacek.fxradio.integration.Elements.stationsHistory
import online.hudacek.fxradio.integration.Elements.volumeMaxIcon
import online.hudacek.fxradio.integration.Elements.volumeMinIcon
import online.hudacek.fxradio.integration.Elements.volumeSlider
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.*
import org.apache.logging.log4j.Level
import org.controlsfx.glyphfont.Glyph
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop
import org.testfx.util.WaitForAsyncUtils
import tornadofx.DataGrid
import tornadofx.find
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Basic functionality test
 * macOS: enable IntelliJ in Settings > Privacy > Accessibility to make it work
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@ExtendWith(ApplicationExtension::class)
@DisplayName("Basic functionality tests for the FXRadio application")
class AppFunctionalityTest {

    private val robot: FxRobot = FxRobot()

    private lateinit var app: FxRadio

    // Stations service, init needed only once
    private val service = ServiceProvider("https://${Config.API.fallbackApiServerURL}").create<RadioBrowserApi>()

    @Start
    fun start(stage: Stage) {
        app = FxRadio(stylesheet = Styles::class, isAppRunningInTest = true)
        app.start(stage)

        // Disable app logger to have only relevant logs
        val logViewModel = find<LogViewModel>()
        logViewModel.item = Log(Level.INFO)
        logViewModel.commit()
    }

    @Stop
    fun stop() = app.stop()

    @Test
    @Order(1)
    fun `app should play and pause selected station`() {
        // Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        // Get Instance of player
        val player = find<PlayerViewModel>()
        val selectedStationViewModel = find<SelectedStationViewModel>()

        // Wait for stations to load
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>

        waitFor(5) {
            stations.isVisible && stations.items.size > 0
        }

        //Avoid station names that start with # as it is query locator for ID
        val stationToClick = stations.items
            .filter { !it.name.startsWith("#") }
            .filter { it.name != selectedStationViewModel.stationProperty.value.name }
            .take(5)
            .random()
        robot.doubleClickOn(stationToClick.name)

        WaitForAsyncUtils.waitForFxEvents()

        // Wait for stream start
        waitFor(5) { player.stateProperty.value is PlayerState.Playing }

        // Check that player has text with name of the station
        verifyThat(nowPlayingLabel, hasLabel(stationToClick.name))

        // Stop the stream
        val stopButton = robot.find(playerControls) as Glyph
        robot.clickOn(stopButton)

        WaitForAsyncUtils.waitForFxEvents()

        // Wait until stream is stopped
        waitFor(2) { player.stateProperty.value == PlayerState.Stopped }

        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))
    }

    @Test
    fun `api test`() {
        // Wait for stations to load
        val appStations = robot.find(stationsDataGrid) as DataGrid<Station>

        // Get results from API
        val apiStations = service.getTopVotedStations().blockingGet()
            .filter { it.countryCode != "RU" }

        assertEquals(apiStations.size, appStations.items.size)
    }

    @Test
    fun `history tab should show same stations as in database`() {
        val historyItemIndex = 3

        // Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        // Find libraries item
        val libraries = robot.find(libraryListView) as ListView<LibraryItem>

        // Wait until whole app is loaded
        sleep(2)

        // Click on History item in Library List Item
        val historyItem = robot.from(libraries)
            .lookup(libraries.items[historyItemIndex].type.key.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            })
            .query<Label>()
        robot.clickOn(historyItem)

        // Find DataGrid, History List and actual db count
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        val historydbCount = Tables.history.selectAll().count().blockingGet()
        val stationsHistory = robot.find(stationsHistory) as ListView<Station>

        // Stations datagrid is not visible in history
        waitFor(2) { !stations.isVisible }

        // Instead, list of stations is visible
        waitFor(2) { stationsHistory.isVisible }

        // Verify DB count equals actual list items count
        assertEquals(historydbCount.toInt(), stationsHistory.items.size)
    }

    @Test
    fun `click on volume icons should change slider value`() {
        val sliderMinValueExpected = -30.0
        val sliderMaxValueExpected = 5.0

        // Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        // Verify volume icons near slider are visible
        verifyThat(volumeMinIcon, visible())
        verifyThat(volumeMaxIcon, visible())

        //Verify volume icon click changes the slider value
        robot.clickOn(volumeMinIcon)
        val slider = robot.find(volumeSlider) as Slider
        waitFor(2) { slider.value == sliderMinValueExpected }

        //Verify volume icon click changes the slider value
        robot.clickOn(volumeMaxIcon)
        waitFor(2) { slider.value == sliderMaxValueExpected }
    }

    @Test
    fun `search should show correct results`() {
        // Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        // Verify search field is present
        verifyThat(search, visible())

        // Verify short query UI
        robot.enterText(search, "st")
        verifyThat(stationMessageHeader, visible())
        verifyThat(stationMessageSubHeader, visible())
        verifyThat(search, hasValue("st"))
        verifyThat(stationMessageHeader, hasText("Searching Radio Directory"))

        // Enter query into field
        robot.enterText(search, "station")
        verifyThat(search, hasValue("station"))

        // Wait until DataGrid is loaded with stations for the provided search query
        sleep(8)

        verifyThat(stationMessageHeader, visible())
    }

    @Test
    fun `pinned country should appear in pinned list`() {
        // Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        // Simulate add country to pinned list
        robot.interact {
            val testCountry = Country("TestPinnedCountryName", "AF", 250)
            val library = find<LibraryViewModel>()
            logger.info { "Pin country $testCountry" }
            library.pinCountry(testCountry)
        }

        val countries = robot.findAll<ListView<Country>>(libraryCountriesFragment).toList()

        // Find "TestPinnedCountryName" in the list of items
        val item = robot.from(countries[0])
            .lookup("TestPinnedCountryName")
            .query<Label>()

        // Simulate remove country to pinned list
        robot.interact {
            val library = find<LibraryViewModel>()
            logger.info { "Unpin item: $item" }
            library.unpinCountry(Country(item.text, "AF", 250))
        }

        // Check there is no item with this label in the app
        val items = robot.from(countries[0])
            .lookup("TestPinnedCountryName")
            .queryAll<Label>()
        waitFor(2) { items.size == 0 }
    }
}
