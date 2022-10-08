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

/**
 * App configuration values/flags
 */
object Config {

    object Resources {
        const val appLogo = "radio-icon-applogo.png"
        const val stageIcon = "radio-logo-stageicon.png"
        const val waveIcon = "radio-logo-big.png"
    }

    object API {
        const val dnsLookupURL = "all.api.radio-browser.info"
        const val fallbackApiServerURL = "de1.api.radio-browser.info"
        const val repositoryURL = "https://github.com/Joseph5610/fxradio-main/"
        const val radioBrowserUrl = "https://radio-browser.info"
    }

    /**
     * Paths to common locations
     * All user files should be stored in $USER_HOME/.fxradio/ directory
     */
    object Paths {
        private val appName = FxRadio.appName.lowercase()

        val baseAppPath = System.getProperty("user.home") + "/.$appName"
        val confDirPath = "$baseAppPath/conf"
        val cacheDirPath = "$baseAppPath/cache"
        val dbPath = "$baseAppPath/$appName.db"
    }

    /**
     * Flags that change app behaviour
     * Experimental features
     */
    object Flags {

    }
}
