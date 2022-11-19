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

import io.reactivex.Observable
import io.reactivex.Single
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.util.applySchedulers
import online.hudacek.fxradio.util.applySchedulersSingle
import org.nield.rxkotlinjdbc.SelectOperation

/**
 * Common operations on database of stations with different tables (e.g. History, Favourites ..)
 */
abstract class StationTable(override val tableName: String) : Table<Station>, Database(tableName) {

    override fun selectAll(): Observable<Station> = selectAllQuery().toStationObservable()

    override fun removeAll(): Single<Int> = removeAllQuery().toSingle().compose(applySchedulersSingle())

    override fun remove(element: Station): Single<Station> =
        removeQuery("DELETE FROM $tableName WHERE stationuuid = ?")
            .parameter(element.stationuuid)
            .toSingle()
            .map { element }

    fun SelectOperation.toStationObservable(): Observable<Station> = toObservable {
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
    }.compose(applySchedulers())
}
