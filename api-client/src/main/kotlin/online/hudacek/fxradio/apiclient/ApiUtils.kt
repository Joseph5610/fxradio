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

package online.hudacek.fxradio.apiclient

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object ApiUtils {

    /**
     * Stations from those countries are filtered in the whole application
     */
    internal val COUNTRY_IGNORE_LIST = listOf("RU", "BY", "PS")

    /**
     * Returns true if the [url] is valid HTTP or HTTPS URL
     */
    fun isValidUrl(url: String) = url.toHttpUrlOrNull() != null
}
