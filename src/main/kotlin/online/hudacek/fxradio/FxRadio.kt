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
import online.hudacek.fxradio.model.LogLevel
import online.hudacek.fxradio.model.LogLevelModel
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.views.MainView
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Main class of the app
 * main() method should be run to start the app
 */
class FxRadio : App(MainView::class, Styles::class) {

    //override app.config path to user.home/fxradio
    override val configBasePath: Path = Paths.get(Config.Paths.confDirPath)

    private val logLevel: LogLevelModel by inject()

    override fun start(stage: Stage) {
        with(stage) {
            minWidth = 600.0
            minHeight = 400.0
            super.start(this)
        }

        //init logger
        logLevel.item = LogLevel(Level.valueOf(config.string(Config.Keys.logLevel, "INFO")))
        Configurator.setAllLevels(LogManager.getRootLogger().name, logLevel.level.value)
    }

    /**
     * Basic info about the app
     */
    companion object {
        const val appName = "FXRadio"
        const val appDesc = "Internet radio directory"
        const val appUrl = "https://hudacek.online/fxradio/"
        const val author = "Jozef Hudáček"
        const val copyright = "Copyright (c) 2020"

        /**
         * Get version from jar MANIFEST.MF file
         */
        val version: String by lazy {
            FxRadio::class.java.getPackage().implementationVersion ?: "DEVELOPMENT"
        }
    }
}

fun main(args: Array<String>) = launch<FxRadio>(args)
