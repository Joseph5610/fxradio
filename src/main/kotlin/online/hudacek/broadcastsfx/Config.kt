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

object Config {

    object Paths {
        private val appNamePath = About.appName.toLowerCase()

        val appConfig = System.getProperty("user.home") + "/.$appNamePath/conf"
        val imageCache = System.getProperty("user.home") + "/.$appNamePath/cache"
        val db = System.getProperty("user.home") + "/.$appNamePath/" + About.appName.toLowerCase() + ".db"
    }

    object Keys {
        const val useNativeMenuBar = "menu.native"
        const val volume = "player.volume"
        const val playerType = "player.type"
        const val apiServer = "app.server"
        const val searchQuery = "search.query"
        const val playerAnimate = "player.animate"
    }

    object Flags {
        const val addStationEnabled = false
    }
}