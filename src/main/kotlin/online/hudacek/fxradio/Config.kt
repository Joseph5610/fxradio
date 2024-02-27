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
 * Static configuration values
 */
object Config {

    object Resources {
        const val APP_LOGO = "radio-icon-applogo.png"
        const val APP_STAGE_ICON = "radio-logo-stageicon.png"
        const val APP_TRAY_ICON = "/radio-icon-applogo.png"
        const val APP_WAVE_ICON = "radio-logo-big.png"
    }

    object API {
        const val DNS_LOOKUP_URL = "all.api.radio-browser.info"
        const val FALLBACK_URL = "nl1.api.radio-browser.info"
        const val BASE_URL = "https://radio-browser.info"
        const val MUSICBRAINZ_URL = "https://musicbrainz.org/ws/2/"
        const val COVER_ART_URL = "https://coverartarchive.org/release/"
    }

    /**
     * Paths to common locations
     * All user files should be stored in $USER_HOME/.fxradio/ directory
     */
    object Paths {
        private val appName = FxRadio.APP_NAME.lowercase()

        val baseAppPath = System.getProperty("user.home") + "/.$appName"
        val confDirPath = "$baseAppPath/conf"
        val cacheDirPath = "$baseAppPath/cache"
        val dbPath = "$baseAppPath/$appName.db"
    }
}
