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

package online.hudacek.broadcastsfx

import javafx.stage.Stage
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import online.hudacek.broadcastsfx.model.rest.Station
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.matcher.control.LabeledMatchers
import org.testfx.util.WaitForAsyncUtils
import tornadofx.*
import java.util.concurrent.TimeUnit

@ExtendWith(ApplicationExtension::class)
class ApiTest {

    private lateinit var app: FxRadio

    @Start
    fun start(stage: Stage) {
        app = FxRadio()
        app.start(stage)
    }

    /**
     * Basic interactions test
     * macOS: enable IntelliJ in Settings > Privacy > Accesibility to make it work
     */
    @Test
    fun basicTest(robot: FxRobot) {
        val mediaPlayerWrapper = find<MediaPlayerWrapper>()

        verifyThat("#nowStreaming", LabeledMatchers.hasText("Streaming stopped"))

        //Wait for stations to load
        val stations = robot.lookup("#stations").query<DataGrid<Station>>()
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS) {
            stations.isVisible && stations.items.size > 1
        }

        //wait until loaded
        WaitForAsyncUtils.sleep(2, TimeUnit.SECONDS)
        robot.clickOn(stations.items[0].name)

        WaitForAsyncUtils.sleep(5, TimeUnit.SECONDS)
        Assertions.assertTrue(mediaPlayerWrapper.playingStatus == PlayingStatus.Playing)
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