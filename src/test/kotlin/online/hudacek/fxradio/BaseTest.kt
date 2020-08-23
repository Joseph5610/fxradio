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

package online.hudacek.fxradio

import javafx.stage.Stage
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.Station
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop
import tornadofx.*

@ExtendWith(ApplicationExtension::class)
class BaseTest {

    private lateinit var app: FxRadio

    private val nowPlayingLabel = "#nowStreaming"
    private val stationsDataGrid = "#stations"

    @Start
    fun start(stage: Stage) {
        app = FxRadio()
        app.start(stage)
    }

    @Stop
    fun stop() {
        app.stop()
    }

    /**
     * Basic interactions test
     * macOS: enable IntelliJ in Settings > Privacy > Accessibility to make it work
     */
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
    }

    @Test
    fun apiTest() {
        StationsApi.client
                .getTopStations()
                .subscribe { stations ->
                    Assertions.assertEquals(50, stations.size)
                    stations.forEach {
                        //top 50 stations should not have empty URL and have name
                        Assertions.assertTrue(it.name.isNotEmpty())
                        Assertions.assertTrue(it.url_resolved != null)
                    }
                }.dispose()
    }
}