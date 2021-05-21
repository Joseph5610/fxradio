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

import javafx.stage.Stage
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.FxRadioDark
import online.hudacek.fxradio.util.macos.NSMenu
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Init
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop

@ExtendWith(ApplicationExtension::class)
class DarkModeTests {

    private lateinit var app: FxRadio

    private val nowPlayingLabel = "#nowStreaming"
    private val stationsDataGrid = "#stations"

    @Init
    fun init() {
        NSMenu.isInTest = true
    }

    @Start
    fun start(stage: Stage) {
        app = FxRadioDark()
        app.start(stage)
    }

    @Stop
    fun stop() = app.stop()

    @Test
    fun darkModeTest(robot: FxRobot) {
        FxAssert.verifyThat(nowPlayingLabel, hasLabel("Streaming stopped"))
        //TODO check app in dark mode
    }
}