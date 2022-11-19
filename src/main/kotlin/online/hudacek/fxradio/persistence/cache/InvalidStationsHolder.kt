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

package online.hudacek.fxradio.persistence.cache

import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import tornadofx.observableListOf

/**
 * Holds list of stations that have invalid station logo
 * Purpose is to not try to download it each time the station is visible in the grid,
 * instead it is simply ignored until next start of the app as the list is not persistent
 */
object InvalidStationsHolder {

    val invalidLogoStations = observableListOf(Station.dummy)

    fun Station.setInvalidLogo() {
        if (!hasInvalidLogo()) {
            invalidLogoStations += this
        }
    }

    fun Station.hasInvalidLogo() = this in invalidLogoStations
}