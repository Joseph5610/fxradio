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

import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.FxRadioLight
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.PlayingStatus
import online.hudacek.fxradio.macos.MacMenu
import online.hudacek.fxradio.media.MediaPlayerWrapper
import online.hudacek.fxradio.storage.Database
import online.hudacek.fxradio.viewmodel.LibraryItem
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop
import org.testfx.util.WaitForAsyncUtils
import tornadofx.*

/**
 * Basic interactions test
 * macOS: enable IntelliJ in Settings > Privacy > Accessibility to make it work
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@ExtendWith(ApplicationExtension::class)
class BasicFunctionalityTests {

    private lateinit var app: FxRadio

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
    @Order(1)
    fun basicPlayPauseTest(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasText("Streaming stopped"))

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
            MediaPlayerWrapper.playingStatus == PlayingStatus.Playing
        }

        val stopButton = robot.find(playerControls) as Button
        robot.clickOn(stopButton)

        WaitForAsyncUtils.waitForFxEvents()

        //Wait for stream stop
        waitFor(2) {
            MediaPlayerWrapper.playingStatus == PlayingStatus.Stopped
        }

        verifyThat(nowPlayingLabel, hasText("Streaming stopped"))
    }

    @Test
    fun testHistoryTab(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasText("Streaming stopped"))

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

        val historydbCount = Database.History.get().blockingGet().size

        if (historydbCount == 0) {
            //Stations library is containing all stations
            waitFor(2) {
                !stations.isVisible
            }
        } else {
            waitFor(2) {
                stations.isVisible
            }

            Assertions.assertTrue(stations.items.size == historydbCount)
        }
    }

    @Test
    fun testVolumeSliderIcons(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasText("Streaming stopped"))

        //Wait for stations to load
        val minIcon = robot.find(volumeMinIcon) as Button
        val maxIcon = robot.find(volumeMaxIcon) as Button
        waitFor(1) {
            minIcon.isVisible
            maxIcon.isVisible
        }

        robot.clickOn(minIcon)

        val slider = robot.find(volumeSlider) as Slider
        waitFor(2) {
            slider.value == -30.0
        }

        robot.clickOn(maxIcon)
        waitFor(2) {
            slider.value == 5.0
        }
    }

    @Test
    fun testSearch(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasText("Streaming stopped"))
        val searchField = robot.find(search) as TextField
        waitFor(1) {
            searchField.isVisible
        }
        robot.doubleClickOn(searchField)
        robot.press(KeyCode.DELETE)
        robot.write("sta")

        val msgHeader = robot.find(stationMessageHeader) as Label
        val msgSubHeader = robot.find(stationMessageSubHeader) as Label

        waitFor(2) {
            searchField.text == "sta" && msgHeader.isVisible && msgSubHeader.isVisible
        }

        robot.doubleClickOn(searchField)
        robot.press(KeyCode.DELETE)
        robot.write("station")

        waitFor(1) {
            searchField.text == "station"
        }

        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        waitFor(5) {
            stations.isVisible && stations.items.size > 1
        }
    }
}