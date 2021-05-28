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
import online.hudacek.fxradio.api.http.HttpClient
import online.hudacek.fxradio.api.stations.StationsApi
import online.hudacek.fxradio.ui.CustomErrorHandler
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.style.StylesDark
import online.hudacek.fxradio.ui.view.MainView
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Tray
import online.hudacek.fxradio.util.macos.MacUtils
import online.hudacek.fxradio.util.saveProperties
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

/**
 * Load app in Dark Mode
 */
class FxRadioDark : FxRadio(darkModeEnabled = true, stylesheet = StylesDark::class)

/**
 * Load app in Light Mode
 */
class FxRadioLight : FxRadio(stylesheet = Styles::class)

/**
 * Load the app with provided [stylesheet] class
 */
open class FxRadio(val darkModeEnabled: Boolean = false,
                   stylesheet: KClass<out Stylesheet>) : App(MainView::class, stylesheet) {

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
            StationsApi.serviceProvider.close()
            HttpClient.close()
        }

        //Save last used window width/height on close of the app to use it on next start
        saveProperties(mapOf(
                Properties.WindowWidth to primaryStage.width,
                Properties.WindowHeight to primaryStage.height,
                Properties.WindowX to primaryStage.x,
                Properties.WindowY to primaryStage.y
        ))
        super.stop()
    }

    /**
     * Basic info about the app
     */
    companion object : Component() {

        var isTestEnvironment = false

        const val appName = "FXRadio"
        const val appDesc = "Internet radio directory"
        const val appUrl = "https://hudacek.online/fxradio/"
        const val author = "hudacek.online"
        const val copyright = "Copyright (c) 2020"

        val darkModeEnabled by lazy { (app as FxRadio).darkModeEnabled }

        /**
         * Gets version from jar MANIFEST.MF file
         * On failure (e.g if app is not run from the jar file), returns the "0.0-DEVELOPMENT" value
         */
        val version: String by lazy {
            FxRadio::class.java.getPackage().implementationVersion ?: "0.0-DEVELOPMENT"
        }
    }
}

/**
 * Main starting method for the App
 */
fun main(args: Array<String>) {
    val isSystemDarkMode: Boolean = if (MacUtils.isMac) {
        MacUtils.isSystemDarkMode
    } else {
        false
    }

    if (Config.Flags.darkStylesEnabled && isSystemDarkMode) {
        launch<FxRadioDark>(args)
    } else {
        launch<FxRadioLight>(args)
    }
}
