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
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.MainView
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Main class for the app
 */
class Broadcasts : App(MainView::class, Styles::class) {
    //override app.config path to user.home/fxradio
    override val configBasePath: Path = Paths.get(Config.Paths.appConfig)

    override fun start(stage: Stage) {
        with(stage) {
            minWidth = 600.0
            minHeight = 400.0
            super.start(this)
        }
    }

    companion object {
        fun getVersion(): String {
            return Broadcasts::class.java.getPackage().implementationVersion ?: "DEVELOPMENT"
        }
    }
}

fun main(args: Array<String>) = launch<Broadcasts>(args)
