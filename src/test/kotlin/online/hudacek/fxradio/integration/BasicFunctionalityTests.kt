/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.integration

import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.Slider
import javafx.stage.Stage
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.FxRadioLight
import online.hudacek.fxradio.api.ApiServiceProvider
import online.hudacek.fxradio.api.stations.StationsApi
import online.hudacek.fxradio.api.stations.model.SearchBody
import online.hudacek.fxradio.api.stations.model.Station
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.viewmodel.LibraryItem
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
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
class BasicFunctionalityTests {

    private lateinit var app: FxRadio
    private lateinit var stage: Stage

    companion object {
        //IDs
        private const val nowPlayingLabel = "#nowStreaming"
        private const val stationsDataGrid = "#stations"
        private const val stationsHistory = "#stationsHistoryList"
        private const val libraryListView = "#libraryListView"
        private const val volumeMinIcon = "#volumeMinIcon"
        private const val volumeMaxIcon = "#volumeMaxIcon"
        private const val volumeSlider = "#volumeSlider"
        private const val playerControls = "#playerControls"
        private const val search = "#search"
        private const val stationMessageHeader = "#stationMessageHeader"
        private const val stationMessageSubHeader = "#stationMessageSubHeader"
    }

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
        this.stage = stage
    }

    @Stop
    fun stop() = app.stop()

    @Test
    fun apiTest(robot: FxRobot) {
        val stations = service.getTopStations().blockingGet()
        Assertions.assertEquals(50, stations.size)

        //Wait for stations to load
        val appStations = robot.find(stationsDataGrid) as DataGrid<Station>

        stations.forEachIndexed { index, station ->
            //top 50 stations should not have empty URL and have name

            Assertions.assertTrue(station.name.isNotEmpty())
            Assertions.assertTrue(station.url_resolved != null)
            Assertions.assertEquals(stations[index].name, appStations.items[index].name)
        }
    }

    @Test
    @Order(1)
    fun basicPlayPauseTest(robot: FxRobot) {
        //Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Get Instance of player
        val player = find<PlayerViewModel>()

        //Wait for stations to load
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        waitFor(5) {
            stations.isVisible && stations.items.size > 1
        }

        //wait until loaded
        sleep(2)

        //Avoid station names that start with # as it is query locator for ID
        val stationToClick = stations.items
                .filter { !it.name.startsWith("#") }
                .filter { it.name != player.stationProperty.value.name }
                .take(5)
                .random()
        robot.doubleClickOn(stationToClick.name)

        WaitForAsyncUtils.waitForFxEvents()

        //Wait for stream start
        waitFor(5) {
            player.stateProperty.value == PlayerState.Playing
        }

        val stopButton = robot.find(playerControls) as Button
        robot.clickOn(stopButton)

        WaitForAsyncUtils.waitForFxEvents()

        //Wait for stream stop
        waitFor(2) {
            player.stateProperty.value == PlayerState.Stopped
        }

        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))
    }

    @Test
    fun testHistoryTab(robot: FxRobot) {
        //Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Wait for stations to load
        val libraries = robot.find(libraryListView) as ListView<LibraryItem>
        waitFor(10) {
            libraries.items.size == 3
        }

        //wait until loaded
        sleep(2)

        //Click on History in Library List Item
        val historyItem = robot.from(libraries)
                .lookup(libraries.items[2].type.key.capitalize())
                .query<SmartListCell<LibraryItem>>()
        robot.clickOn(historyItem)

        //Find DataGrid, History List and actual db count
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        val historydbCount = Tables.history.selectAll().count().blockingGet()
        val stationsHistory = robot.find(stationsHistory) as ListView<Station>

        //Stations datagrid is not visible in history
        waitFor(2) {
            !stations.isVisible
        }

        //Instead, list of stations is visible
        waitFor(2) {
            stationsHistory.isVisible
        }

        //Verify DB count equals actual list items count
        Assertions.assertEquals(historydbCount.toInt(), stationsHistory.items.size)
    }

    @Test
    fun testVolumeSliderIcons(robot: FxRobot) {
        //Verify app initial state
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Wait for stations to load
        verifyThat(volumeMinIcon, visible())
        verifyThat(volumeMaxIcon, visible())

        //Verify volume icon click changes the slider value
        robot.clickOn(volumeMinIcon)
        val slider = robot.find(volumeSlider) as Slider
        waitFor(2) {
            slider.value == -30.0
        }

        //Verify volume icon click changes the slider value
        robot.clickOn(volumeMaxIcon)
        waitFor(2) {
            slider.value == 5.0
        }
    }

    @Test
    fun testSearch(robot: FxRobot) {
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

        //Wait until datagrid is loaded with stations for the provided searchquery
        sleep(5)

        //Get stations in DataGrid
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        verifyThat(stationMessageHeader, visible())

        //Compare results from API and APP
        println("Search results displayed: " + stations.items.size)
        println("Search Results from API: " + stationResults.size)
        Assertions.assertEquals(stationResults.size, stations.items.size)
    }
}