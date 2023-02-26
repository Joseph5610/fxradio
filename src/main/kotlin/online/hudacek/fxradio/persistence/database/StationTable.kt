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

import io.reactivex.rxjava3.core.Flowable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import org.davidmoten.rxjava3.jdbc.SelectBuilder

/**
 * Common operations on database of stations with different tables (e.g. History, Favourites ..)
 */
abstract class StationTable(override val tableName: String) : Table<Station>, Database(tableName) {

    override fun selectAll() = selectAllQuery().asStationFlowable()

    override fun removeAll(): Flowable<Int> = removeAllQuery().counts()

    override fun remove(element: Station): Flowable<Int> =
        database.update("DELETE FROM $tableName WHERE stationuuid = ?")
            .parameter(element.uuid)
            .counts()

    /**
     * Map ResultSet to [Station] Flowable
     */
    fun SelectBuilder.asStationFlowable(): Flowable<Station> = get {
        Station(
            it.getString("stationuuid"),
            it.getString("name"),
            it.getString("url_resolved"),
            it.getString("homepage"),
            it.getString("favicon"),
            it.getString("tags"),
            it.getString("country"),
            it.getString("countrycode"),
            it.getString("state"),
            it.getString("language"),
            it.getString("codec"),
            it.getInt("bitrate")
        )
    }
}
