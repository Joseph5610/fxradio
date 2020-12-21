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

import com.vdurmont.semver4j.Semver
import javafx.stage.Stage
import online.hudacek.fxradio.api.HttpClientHolder
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.ui.CustomErrorHandler
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.style.StylesDark
import online.hudacek.fxradio.ui.view.MainView
import online.hudacek.fxradio.ui.viewmodel.LogModel
import online.hudacek.fxradio.ui.viewmodel.LogViewModel
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import online.hudacek.fxradio.utils.asLevel
import online.hudacek.fxradio.utils.isSystemDarkMode
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

/**
 * Main class of the app
 * main() method should be run to start the app
 */

//Dark mode
class FxRadioDark : FxRadio(StylesDark::class) {
    override var useDarkModeStyle = true
}

//Light mode
class FxRadioLight : FxRadio(Styles::class) {
    override var useDarkModeStyle = false
}

open class FxRadio(stylesheet: KClass<out Stylesheet>) : App(MainView::class, stylesheet) {

    //override app.config path to user.home/fxradio
    override val configBasePath: Path = Paths.get(Config.Paths.confDirPath)

    private val logViewModel: LogViewModel by inject()

    open var useDarkModeStyle: Boolean by singleAssign()

    override fun start(stage: Stage) {
        Thread.setDefaultUncaughtExceptionHandler(CustomErrorHandler())

        with(stage) {
            minWidth = 600.0
            minHeight = 400.0
            super.start(this)
        }

        //init logger level based on stored settings
        val savedLevel = Property(Properties.LOG_LEVEL).get("INFO").asLevel()
        logViewModel.item = LogModel(savedLevel)
        logViewModel.commit()
    }

    /**
     * Basic info about the app
     */
    companion object : Component() {

        const val appName = "FXRadio"
        const val appDesc = "Internet radio directory"
        const val appUrl = "https://hudacek.online/fxradio/"
        const val author = "hudacek.online"
        const val copyright = "Copyright (c) 2020"

        val isAppInDarkMode by lazy {
            (app as FxRadio).useDarkModeStyle
        }

        /**
         * Get version from jar MANIFEST.MF file
         */
        val version: Version by lazy {
            Version(FxRadio::class.java.getPackage().implementationVersion ?: "0.1-DEVELOPMENT")
        }

        fun shutDown() {
            StationsApi.client.shutdown()
            HttpClientHolder.client.shutdown()
        }
    }
}

data class Version(val version: String) : Semver(version, SemverType.LOOSE)

fun main(args: Array<String>) {
    if (Config.Flags.darkStylesEnabled && isSystemDarkMode) {
        launch<FxRadioDark>(args)
    } else {
        launch<FxRadioLight>(args)
    }
}
