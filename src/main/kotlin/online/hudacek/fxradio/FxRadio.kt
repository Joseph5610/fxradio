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
import online.hudacek.fxradio.FxRadio.Companion.isDarkModePreferred
import online.hudacek.fxradio.api.StationsProvider
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.ui.CustomErrorHandler
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.style.StylesDark
import online.hudacek.fxradio.ui.view.MainView
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Tray
import online.hudacek.fxradio.util.macos.MacUtils
import online.hudacek.fxradio.util.saveProperties
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import org.apache.logging.log4j.LogManager
import tornadofx.App
import tornadofx.FX
import tornadofx.Stylesheet
import tornadofx.launch
import tornadofx.stylesheet
import java.io.FileInputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Year
import kotlin.reflect.KClass

/**
 * Load app in Dark Mode
 */
class FxRadioDark : FxRadio(stylesheet = StylesDark::class)

/**
 * Load app in Light Mode
 */
class FxRadioLight : FxRadio(stylesheet = Styles::class)

/**
 * Load the app with provided [stylesheet] class
 */
open class FxRadio(stylesheet: KClass<out Stylesheet>) : App(MainView::class, stylesheet) {

    private val playerViewModel: PlayerViewModel by inject()

    /**
     * override app.config path to $user.home/fxradio
     */
    override val configBasePath: Path = Paths.get(Config.Paths.confDirPath)

    override fun start(stage: Stage) {
        Thread.setDefaultUncaughtExceptionHandler(CustomErrorHandler())
        with(stage) {
            minWidth = 600.0
            minHeight = 400.0

            //Setup window location on screen
            config.double(Properties.WindowWidth.key)?.let {
                width = it
            }
            config.double(Properties.WindowHeight.key)?.let {
                height = it
            }
            config.double(Properties.WindowX.key)?.let {
                x = it
            }
            config.double(Properties.WindowY.key)?.let {
                y = it
            }
            super.start(this)
        }
        Tray().addIcon()
    }

    override fun stop() {
        if (!isTestEnvironment) {
            playerViewModel.releasePlayer()
            StationsProvider.serviceProvider.close()
            HttpClient.close()
            LogManager.shutdown()
        }

        //Save last used window width/height on close of the app to use it on next start
        saveProperties(mapOf(
                Properties.WindowWidth to FX.primaryStage.width,
                Properties.WindowHeight to FX.primaryStage.height,
                Properties.WindowX to FX.primaryStage.x,
                Properties.WindowY to FX.primaryStage.y
        ))
        super.stop()
    }

    /**
     * Basic info about the app
     */
    companion object {

        var isTestEnvironment = false

        const val appName = "FXRadio"
        const val appDesc = "Internet radio directory"
        const val appUrl = "https://hudacek.online/fxradio/"
        const val author = "hudacek.online"
        val copyright = "Copyright (c) 2020-" + Year.now().value

        /**
         * Gets version from jar MANIFEST.MF file
         * On failure (e.g if app is not run from the jar file), returns the "0.0-DEVELOPMENT" value
         */
        val version: String by lazy {
            FxRadio::class.java.getPackage().implementationVersion ?: "0.0-DEVELOPMENT"
        }

        private fun hasSystemDarkMode() = if (MacUtils.isMac) {
            MacUtils.isSystemDarkMode
        } else {
            false
        }

        fun isDarkModePreferred(): Boolean {
            //we have to use the ugly java way to access this property as we want to access it
            //in the time that the app is not yet instantiated
            val fis = FileInputStream(Config.Paths.confDirPath + "/app.properties")
            val props = java.util.Properties()
            props.load(fis)
            val darkModeProp = props.getProperty("app.darkmode")
            return darkModeProp?.toBoolean() ?: hasSystemDarkMode()
        }
    }
}

/**
 * Main starting method for the App
 */
fun main(args: Array<String>) {
    if (isDarkModePreferred()) {
        launch<FxRadioDark>(args)
    } else {
        launch<FxRadioLight>(args)
    }
}
