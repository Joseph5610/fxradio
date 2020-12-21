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
import online.hudacek.fxradio.storage.Database
import online.hudacek.fxradio.ui.viewmodel.LibraryItem
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import online.hudacek.fxradio.ui.viewmodel.PlayingStatus
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
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

    //IDs
    private val nowPlayingLabel = "#nowStreaming"
    private val stationsDataGrid = "#stations"
    private val libraryListView = "#libraryListView"
    private val volumeMinIcon = "#volumeMinIcon"
    private val volumeMaxIcon = "#volumeMaxIcon"
    private val volumeSlider = "#volumeSlider"
    private val playerControls = "#playerControls"
    private val search = "#search"
    private val stationMessageHeader = "#stationMessageHeader"
    private val stationMessageSubHeader = "#stationMessageSubHeader"

    //Http Client, init only once needed
    private val service by lazy { StationsApi.service }

    init {
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
    fun apiTest() {
        service.getTopStations()
                .subscribe { stations ->
                    Assertions.assertEquals(50, stations.size)
                    stations.forEach {
                        //top 50 stations should not have empty URL and have name
                        Assertions.assertTrue(it.name.isNotEmpty())
                        Assertions.assertTrue(it.url_resolved != null)
                    }
                }.dispose()
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
        robot.doubleClickOn(stations.items[0].name)

        WaitForAsyncUtils.waitForFxEvents()

        //Wait for stream start
        waitFor(5) {
            player.playingStatusProperty.value == PlayingStatus.Playing
        }

        val stopButton = robot.find(playerControls) as Button
        robot.clickOn(stopButton)

        WaitForAsyncUtils.waitForFxEvents()

        //Wait for stream stop
        waitFor(2) {
            player.playingStatusProperty.value == PlayingStatus.Stopped
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

        val historydbCount = Database.history.select().blockingGet().size

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

        verifyThat(stationMessageHeader, hasText("Searching Your Library"))

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