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

package online.hudacek.fxradio.persistence.database

import online.hudacek.fxradio.Config

/**
 * Object that holds kotlin classes representing SQLite tables
 */
object Database {

    private val dbUrl = "jdbc:sqlite:${Config.Paths.dbPath}"

    private val connectionManager by lazy { ConnectionManager(dbUrl) }

    /**
     * Table for stations saved as favourites
     */
    val favouritesDao by lazy { FavouritesDao(connectionManager.database) }

    /**
     * Table for pinned Countries
     */
    val pinnedCountriesDao by lazy { PinnedCountriesDao(connectionManager.database) }

    fun shutdown() = connectionManager.close()
}
