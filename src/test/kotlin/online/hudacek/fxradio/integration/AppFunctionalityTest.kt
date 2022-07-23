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
import online.hudacek.fxradio.FxRadioLight
import online.hudacek.fxradio.apiclient.ApiServiceProvider
import online.hudacek.fxradio.apiclient.stations.StationsApi
import online.hudacek.fxradio.apiclient.stations.model.Country
import online.hudacek.fxradio.apiclient.stations.model.SearchBody
import online.hudacek.fxradio.apiclient.stations.model.Station
import online.hudacek.fxradio.apiclient.stations.model.isRussia
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
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.viewmodel.LibraryItem
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import org.controlsfx.glyphfont.Glyph
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Init
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop
import org.testfx.util.WaitForAsyncUtils
import tornadofx.DataGrid
import tornadofx.SmartListCell
import tornadofx.find

/**
 * Basic interactions test
 * macOS: enable IntelliJ in Settings > Privacy > Accessibility to make it work
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@ExtendWith(ApplicationExtension::class)
@DisplayName("Basic functionality tests for the FXRadio application")
class AppFunctionalityTest {

    private val logger = KotlinLogging.logger {}

    private val robot: FxRobot = FxRobot()

    private lateinit var app: FxRadio

    //Http Client, init only once needed
    private val service = ApiServiceProvider("https://${Config.API.fallbackApiServerURL}").get<StationsApi>()

    @Init
    fun init() {
        FxRadio.isTestEnvironment = true
    }

    @Start
    fun start(stage: Stage) {
        app = FxRadioLight()
        app.start(stage)
    }

    @Stop
    fun stop() = app.stop()

    @Test
    @Order(1)
    fun `app should play and pause selected station`() {
        //Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Get Instance of player
        val player = find<PlayerViewModel>()

        //Wait for stations to load
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>

        waitFor(5) { stations.isVisible && stations.items.size == 50 }

        //Avoid station names that start with # as it is query locator for ID
        val stationToClick = stations.items
                .filter { !it.name.startsWith("#") }
                .filter { it.name != player.stationProperty.value.name }
                .take(5)
                .random()
        robot.doubleClickOn(stationToClick.name)

        WaitForAsyncUtils.waitForFxEvents()

        //Wait for stream start
        waitFor(5) { player.stateProperty.value == PlayerState.Playing }

        //Check that player has text with name of the station
        verifyThat(nowPlayingLabel, hasLabel(stationToClick.name))

        val stopButton = robot.find(playerControls) as Glyph
        robot.clickOn(stopButton)

        WaitForAsyncUtils.waitForFxEvents()

        //Wait for stream stop
        waitFor(2) { player.stateProperty.value == PlayerState.Stopped }

        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))
    }

    @Test
    fun `api should return same results as in app`() {
        val stations = service.getTopVotedStations().blockingGet()

        Assertions.assertEquals(50, stations.size)

        //Wait for stations to load
        val appStations = robot.find(stationsDataGrid) as DataGrid<Station>

        stations.forEachIndexed { index, _ ->
            Assertions.assertEquals(stations[index].name, appStations.items[index].name)
        }
    }

    @Test
    fun `history tab should show same stations as in database`() {
        val historyItemIndex = 3

        //Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Wait for stations to load
        val libraries = robot.find(libraryListView) as ListView<LibraryItem>

        //wait until loaded
        sleep(2)

        //Click on History in Library List Item
        val historyItem = robot.from(libraries)
                .lookup(libraries.items[historyItemIndex].type.key.capitalize())
                .query<SmartListCell<LibraryItem>>()
        robot.clickOn(historyItem)

        //Find DataGrid, History List and actual db count
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        val historydbCount = Tables.history.selectAll().count().blockingGet()
        val stationsHistory = robot.find(stationsHistory) as ListView<Station>

        //Stations datagrid is not visible in history
        waitFor(2) { !stations.isVisible }

        //Instead, list of stations is visible
        waitFor(2) { stationsHistory.isVisible }

        //Verify DB count equals actual list items count
        Assertions.assertEquals(historydbCount.toInt(), stationsHistory.items.size)
    }

    @Test
    fun `volume icons should change slider value`() {
        //Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Verify volume icons near slider are visible
        verifyThat(volumeMinIcon, visible())
        verifyThat(volumeMaxIcon, visible())

        //Verify volume icon click changes the slider value
        robot.clickOn(volumeMinIcon)
        val slider = robot.find(volumeSlider) as Slider
        waitFor(2) { slider.value == -30.0 }

        //Verify volume icon click changes the slider value
        robot.clickOn(volumeMaxIcon)
        waitFor(2) { slider.value == 5.0 }
    }

    @Test
    fun `search should show correct stations`() {
        //Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Verify search field is present
        verifyThat(search, visible())

        //Verify short query UI
        robot.enterText(search, "st")
        verifyThat(stationMessageHeader, visible())
        verifyThat(stationMessageSubHeader, visible())
        verifyThat(search, hasValue("st"))
        verifyThat(stationMessageHeader, hasText("Searching Radio Directory"))

        //Perform API search
        val stationResults = service.searchStationByName(SearchBody("station")).blockingGet()

        //Enter query into field
        robot.enterText(search, "station")
        verifyThat(search, hasValue("station"))

        //Wait until datagrid is loaded with stations for the provided search query
        sleep(8)

        //Get stations in DataGrid
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        verifyThat(stationMessageHeader, visible())

        //Compare results from API and APP
        logger.info { "Search results displayed: " + stations.items.size }
        logger.info { "Search Results from API: " + stationResults.size }
        waitFor(10) { stationResults.size == stations.items.size }
    }

    @Test
    fun `pinned country should show in pinned list`() {
        //Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Simulate add country to pinned list
        robot.interact {
            val testCountry = Country("TestPinnedCountryName", "AF",250)
            val library = find<LibraryViewModel>()
            logger.info { "Pin country $testCountry" }
            library.pinCountry(testCountry)
        }

        val countries = robot.findAll<ListView<Country>>(libraryCountriesFragment).toList()

        //Find "TestPinnedCountryName" in the list of items
        val item = robot.from(countries[0])
                .lookup("TestPinnedCountryName")
                .query<Label>()

        //Simulate remove country to pinned list
        robot.interact {
            val library = find<LibraryViewModel>()
            logger.info { "Unpin item: $item" }
            library.unpinCountry(Country(item.text, "AF",250))
        }

        //Check there is not item with this label in the app
        val items = robot.from(countries[0])
                .lookup("TestPinnedCountryName")
                .queryAll<Label>()
        waitFor(2) { items.size == 0 }
    }
}