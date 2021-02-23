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
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.FxRadioLight
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.SearchBody
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.macos.MacMenu
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.ui.viewmodel.LibraryItem
import online.hudacek.fxradio.ui.viewmodel.PlayerState
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
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

    companion object {
        //IDs
        private const val nowPlayingLabel = "#nowStreaming"
        private const val stationsDataGrid = "#stations"
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
    private val service by lazy { StationsApi.service }

    @Init
    fun init() {
        MacMenu.isInTest = true
    }

    @Start
    fun start(stage: Stage) {
        app = FxRadioLight()
        app.start(stage)
    }

    @Stop
    fun stop() = app.stop()

    @Test
    fun apiTest(robot: FxRobot) {
        val stations = service.getTopStations().blockingGet()
        Assertions.assertEquals(50, stations.size)
        stations.forEach {
            //top 50 stations should not have empty URL and have name
            Assertions.assertTrue(it.name.isNotEmpty())
            Assertions.assertTrue(it.url_resolved != null)
        }

        //Wait for stations to load
        val appStations = robot.find(stationsDataGrid) as DataGrid<Station>
        Assertions.assertEquals(appStations.items.toList(), stations)
    }

    @Test
    @Order(1)
    fun basicPlayPauseTest(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        val player = find<PlayerViewModel>()

        //Wait for stations to load
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        waitFor(5) {
            stations.isVisible && stations.items.size > 1
        }

        //wait until loaded
        sleep(2)
        //Avoid station names that start with # as it is query locator for ID
        val stationToClick = stations.items.first { !it.name.startsWith("#") }
        robot.doubleClickOn(stationToClick.name)

        WaitForAsyncUtils.waitForFxEvents()

        //Wait for stream start
        waitFor(5) {
            player.playerStateProperty.value == PlayerState.Playing
        }

        val stopButton = robot.find(playerControls) as Button
        robot.clickOn(stopButton)

        WaitForAsyncUtils.waitForFxEvents()

        //Wait for stream stop
        waitFor(2) {
            player.playerStateProperty.value == PlayerState.Stopped
        }

        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))
    }

    @Test
    fun testHistoryTab(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Wait for stations to load
        val libraries = robot.find(libraryListView) as ListView<LibraryItem>
        waitFor(10) {
            libraries.items.size == 3
        }

        //wait until loaded
        sleep(2)

        //Find in list
        val historyItem = robot.from(libraries).lookup(libraries.items[2].type.name).query<SmartListCell<LibraryItem>>()
        robot.clickOn(historyItem)

        val stations = robot.find(stationsDataGrid) as DataGrid<Station>

        val historydbCount = Tables.history.select().blockingGet().size

        if (historydbCount == 0) {
            //Stations library is containing all stations
            waitFor(2) {
                !stations.isVisible
            }
        } else {
            waitFor(2) {
                stations.isVisible
            }

            Assertions.assertEquals(historydbCount, stations.items.size)
        }
    }

    @Test
    fun testVolumeSliderIcons(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Wait for stations to load
        verifyThat(volumeMinIcon, visible())
        verifyThat(volumeMaxIcon, visible())

        robot.clickOn(volumeMinIcon)

        val slider = robot.find(volumeSlider) as Slider
        waitFor(2) {
            slider.value == -30.0
        }

        robot.clickOn(volumeMaxIcon)
        waitFor(2) {
            slider.value == 5.0
        }
    }

    @Test
    fun testSearch(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))

        //Verify search field is present
        verifyThat(search, visible())
        robot.enterText(search, "st")

        verifyThat(stationMessageHeader, visible())
        verifyThat(stationMessageSubHeader, visible())
        verifyThat(search, hasValue("st"))

        verifyThat(stationMessageHeader, hasText("Searching Radio Directory"))

        robot.enterText(search, "station")

        verifyThat(stationMessageHeader, hasText(""))
        verifyThat(stationMessageSubHeader, hasLabel(""))
        verifyThat(search, hasValue("station"))

        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        verifyThat(stationMessageHeader, visible())

        //Perform API search
        val stationResults = service.searchStationByName(SearchBody("station")).blockingGet()

        println("search result items: " + stations.items.size)
        println("search Results: " + stationResults.size)

        Assertions.assertEquals(stationResults.size, stations.items.size)
    }
}