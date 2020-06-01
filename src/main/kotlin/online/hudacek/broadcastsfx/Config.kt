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

/**
 * Basic app configuration
 */
object Config {

    /**
     * Contains paths to common locations
     * All user files should be stored in $USER_HOME/.fxradio/ directory
     */
    object Paths {
        private val appNamePath = FxRadio.appName.toLowerCase()

        const val defaultRadioIcon = "Industry-Radio-Tower-icon.png"

        val baseAppDir = System.getProperty("user.home") + "/.$appNamePath/"
        val appConfig = System.getProperty("user.home") + "/.$appNamePath/conf"
        val imageCache = System.getProperty("user.home") + "/.$appNamePath/cache"
        val db = "$baseAppDir/$appNamePath.db"
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
        const val windowWidth = "window.width"
        const val windowHeight = "window.height"
        const val notifications = "notifications"
    }

    /**
     * Flags that change app behaviour
     */
    object Flags {
        const val addStationEnabled = false
    }
}