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
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.styles.StylesDark
import online.hudacek.fxradio.utils.Version
import online.hudacek.fxradio.utils.isSystemDarkMode
import online.hudacek.fxradio.viewmodel.LogModel
import online.hudacek.fxradio.viewmodel.LogViewModel
import online.hudacek.fxradio.views.MainView
import org.apache.logging.log4j.Level
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

/**
 * Main class of the app
 * main() method should be run to start the app
 */

//Dark mode
class FxRadioDark : FxRadio(StylesDark::class)

//Light mode
class FxRadioLight : FxRadio(Styles::class)

open class FxRadio(stylesheet: KClass<out Stylesheet>) : App(MainView::class, stylesheet) {

    //override app.config path to user.home/fxradio
    override val configBasePath: Path = Paths.get(Config.Paths.confDirPath)

    private val logViewModel: LogViewModel by inject()

    override fun start(stage: Stage) {
        Thread.setDefaultUncaughtExceptionHandler(CustomErrorHandler())
        with(stage) {
            minWidth = 600.0
            minHeight = 400.0
            super.start(this)
        }

        //init logger level based on stored settings
        val savedLevel = Level.valueOf(config.string(Config.Keys.logLevel))
        logViewModel.item = LogModel(savedLevel)
        logViewModel.commit()
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
        val version: Version by lazy {
            Version(FxRadio::class.java.getPackage().implementationVersion ?: "0.1-DEVELOPMENT")
        }
    }
}

fun main(args: Array<String>) {
    if (Config.Flags.darkStylesEnabled && isSystemDarkMode) {
        launch<FxRadioDark>(args)
    } else {
        launch<FxRadioLight>(args)
    }
}
