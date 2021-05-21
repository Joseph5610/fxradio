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

/**
 * App configuration values/flags
 */
object Config {

    object Resources {
        const val appLogo = "appLogo.png"
        const val stageIcon = "Industry-Radio-Tower-icon.png"
        const val waveIcon = "Sound-Wave-icon.png"
    }

    object API {
        const val dnsLookupURL = "all.api.radio-browser.info"
        const val fallbackApiServerURL = "de1.api.radio-browser.info"
    }

    /**
     * Paths to common locations
     * All user files should be stored in $USER_HOME/.fxradio/ directory
     */
    object Paths {
        private val appName = FxRadio.appName.toLowerCase()

        val baseAppPath = System.getProperty("user.home") + "/.$appName/"
        val confDirPath = "$baseAppPath/conf"
        val cacheDirPath = "$baseAppPath/cache"
        val dbPath = "$baseAppPath/$appName.db"
    }

    /**
     * Flags that change app behaviour
     * Experimental features
     */
    object Flags {
        const val darkStylesEnabled = false
        const val useTrayIcon = false
        const val enableStationDebug = true
    }
}