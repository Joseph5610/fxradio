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

package online.hudacek.fxradio.storage.db

import io.reactivex.Observable
import io.reactivex.Single
import online.hudacek.fxradio.apiclient.stations.model.Station

/**
 * Common operations on database of stations with different tables (e.g History, Favourites ..)
 */
class StationTable(override val tableName: String) : Table<Station>, Database(tableName) {

    override fun selectAll(): Observable<Station> = selectAllQuery()
            .toObservable {
                Station(it.getString("stationuuid"),
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
                        it.getInt("bitrate"))
            }

    override fun removeAll(): Single<Int> = removeAllQuery().toSingle()

    override fun insert(element: Station): Single<Station> = insertQuery("INSERT INTO $tableName (name, stationuuid, url_resolved, " +
            "homepage, country, countrycode, state, language, favicon, tags, codec, bitrate) " +
            "VALUES (:name, :stationuuid, :url_resolved, :homepage, :country, :countrycode, :state, :language, :favicon, :tags, :codec, :bitrate )")
            .parameter("name", element.name)
            .parameter("stationuuid", element.stationuuid)
            .parameter("url_resolved", element.url_resolved)
            .parameter("homepage", element.homepage)
            .parameter("country", element.country)
            .parameter("countrycode", element.countrycode)
            .parameter("state", element.state)
            .parameter("language", element.language)
            .parameter("favicon", element.favicon)
            .parameter("tags", element.tags)
            .parameter("codec", element.codec)
            .parameter("bitrate", element.bitrate)
            .toSingle { element }

    override fun remove(element: Station): Single<Station> =
            removeQuery("DELETE FROM $tableName WHERE stationuuid = ?")
                    .parameter(element.stationuuid)
                    .toSingle()
                    .map { element }
}