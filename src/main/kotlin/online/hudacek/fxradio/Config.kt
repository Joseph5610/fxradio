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
 * Basic app configuration
 */
object Config {

    object Resources {
        const val appLogo = "appLogo.png"
        const val defaultRadioIcon = "Industry-Radio-Tower-icon.png"
        const val appWebsiteIcon = "browser-web-icon.png"
    }

    /**
     * Contains paths to common locations
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
     * Keys for values stored in app.properties
     */
    object Keys {
        const val useNativeMenuBar = "menu.native"
        const val volume = "player.volume"
        const val playerType = "player.type"
        const val apiServer = "app.server"
        const val searchQuery = "search.query"
        const val playerAnimate = "player.animate"
        const val notifications = "notifications"
        const val windowDivider = "window.divider"
        const val logLevel = "log.level"
    }

    /**
     * Flags that change app behaviour
     */
    object Flags {
        const val addStationEnabled = false
    }
}