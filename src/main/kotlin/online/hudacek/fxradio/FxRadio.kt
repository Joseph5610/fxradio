/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio

import javafx.scene.image.Image
import javafx.stage.Stage
import online.hudacek.fxradio.FxRadio.Companion.isDarkModePreferred
import online.hudacek.fxradio.api.RBServiceProvider
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.persistence.database.Database
import online.hudacek.fxradio.ui.CustomErrorHandler
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.style.StylesDark
import online.hudacek.fxradio.ui.view.MainView
import online.hudacek.fxradio.ui.view.TrayIcon
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.macos.MacUtils
import online.hudacek.fxradio.util.saveProperties
import online.hudacek.fxradio.util.value
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import org.apache.logging.log4j.LogManager
import tornadofx.App
import tornadofx.FX
import tornadofx.Stylesheet
import tornadofx.launch
import tornadofx.setStageIcon
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

private const val WINDOW_MIN_WIDTH = 600.0
private const val WINDOW_MIN_HEIGHT = 400.0

/**
 * Load the app with provided [stylesheet] class
 */
open class FxRadio(stylesheet: KClass<out Stylesheet>) : App(MainView::class, stylesheet) {

    private val playerViewModel: PlayerViewModel by inject()

    /**
     * override app.config path to ${user.home}/fxradio
     */
    override val configBasePath: Path = Paths.get(Config.Paths.confDirPath)

    private val trayIcon: TrayIcon by lazy { TrayIcon() }

    override fun start(stage: Stage) {
        Thread.setDefaultUncaughtExceptionHandler(CustomErrorHandler())
        with(stage) {
            minWidth = WINDOW_MIN_WIDTH
            minHeight = WINDOW_MIN_HEIGHT

            //Setup window location on screen
            with(config) {
                double(Properties.WindowWidth.key)?.let {
                    width = it
                }
                double(Properties.WindowHeight.key)?.let {
                    height = it
                }
                double(Properties.WindowX.key)?.let {
                    x = it
                }
                double(Properties.WindowY.key)?.let {
                    y = it
                }
            }
            setStageIcon(Image(Config.Resources.stageIcon))
            super.start(this)
        }

        if (Properties.UseTrayIcon.value(true)) {
            trayIcon.addIcon()
        }

        if(!Properties.EnableDebugView.value(false)) {
            // Disable built-in tornadofx layout debugger
            FX.layoutDebuggerShortcut = null
        }
    }

    override fun stop() {
        if (!isTestEnvironment) {
            playerViewModel.releasePlayer()
            RBServiceProvider.close()
            HttpClient.close()
            Database.close()
            LogManager.shutdown()
        }

        //Save last used window width/height on close of the app to use it on next start
        saveProperties(
            mapOf(
                Properties.WindowWidth to FX.primaryStage.width,
                Properties.WindowHeight to FX.primaryStage.height,
                Properties.WindowX to FX.primaryStage.x,
                Properties.WindowY to FX.primaryStage.y
            )
        )
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
         * On failure (e.g. if app is not run from the jar file), returns the "0.0-DEVELOPMENT" value
         */
        val version: String by lazy {
            FxRadio::class.java.getPackage().implementationVersion ?: "0.0-DEVELOPMENT"
        }

        private fun hasSystemDarkMode() = MacUtils.isMac && MacUtils.isSystemDarkMode

        fun isDarkModePreferred(): Boolean {
            return runCatching {
                // We have to use the ugly java way to access this property as we want to access it
                // in the time that the app is not yet instantiated
                val fis = FileInputStream(Config.Paths.confDirPath + "/app.properties")
                val props = java.util.Properties().also { it.load(fis) }
                val darkModeProp = props.getProperty(Properties.DarkMode.key)
                darkModeProp?.toBoolean() ?: hasSystemDarkMode()
            }.getOrDefault(hasSystemDarkMode())
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
