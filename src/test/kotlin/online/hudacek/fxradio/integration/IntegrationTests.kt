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
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.PlayingStatus
import online.hudacek.fxradio.macos.MacMenu
import online.hudacek.fxradio.media.MediaPlayerWrapper
import online.hudacek.fxradio.viewmodel.LibraryItem
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop
import tornadofx.*

/**
 * Basic interactions test
 * macOS: enable IntelliJ in Settings > Privacy > Accessibility to make it work
 */
@ExtendWith(ApplicationExtension::class)
class IntegrationTests {

    private lateinit var app: FxRadio

    private val nowPlayingLabel = "#nowStreaming"
    private val stationsDataGrid = "#stations"
    private val libraryListView = "#libraryListView"
    private val volumeMinIcon = "#volumeMinIcon"
    private val volumeMaxIcon = "#volumeMaxIcon"
    private val volumeSlider = "#volumeSlider"
    private val playerControls = "#playerControls"

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
    fun basicTest(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasText("Streaming stopped"))

        //Wait for stations to load
        val stations = robot.find(stationsDataGrid) as DataGrid<Station>
        waitFor(10) {
            stations.isVisible && stations.items.size > 1
        }

        //wait until loaded
        sleep(2)
        robot.clickOn(stations.items[0].name)

        //Wait for stream start
        waitFor(10) {
            MediaPlayerWrapper.playingStatus == PlayingStatus.Playing
        }

        val stopButton = robot.find(playerControls) as Button
        robot.clickOn(stopButton)

        //Wait for stream stop
        waitFor(10) {
            MediaPlayerWrapper.playingStatus == PlayingStatus.Stopped
        }

        verifyThat(nowPlayingLabel, hasText("Streaming stopped"))
    }

    @Test
    fun checkHistoryTab(robot: FxRobot) {
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

        //Stations library is empty
        waitFor(10) {
            !stations.isVisible
        }
    }

    @Test
    fun checkVolumeSliderIcons(robot: FxRobot) {
        verifyThat(nowPlayingLabel, hasText("Streaming stopped"))

        //Wait for stations to load
        val minIcon = robot.find(volumeMinIcon) as Button
        val maxIcon = robot.find(volumeMaxIcon) as Button
        waitFor(10) {
            minIcon.isVisible
            maxIcon.isVisible
        }

        robot.clickOn(minIcon)

        val slider = robot.find(volumeSlider) as Slider
        waitFor(10) {
            slider.value == -30.0
        }

        robot.clickOn(maxIcon)
        waitFor(10) {
            slider.value == 5.0
        }
    }
}