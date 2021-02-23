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

package online.hudacek.fxradio.storage.db

import io.reactivex.Single
import online.hudacek.fxradio.api.model.Station

/**
 * Common operations on database of stations with different tables
 */
class StationTable(override val tableName: String) : TableOperations<Station> {

    override val createTableSql = "CREATE TABLE IF NOT EXISTS $tableName (ID INTEGER PRIMARY KEY," +
            " stationuuid VARCHAR, name VARCHAR, " +
            " url_resolved VARCHAR, homepage VARCHAR," +
            " favicon VARCHAR, tags VARCHAR, country VARCHAR, " +
            " countrycode VARCHAR, state VARCHAR, language VARCHAR, codec VARCHAR, bitrate INTEGER" +
            " )"

    override fun select(): Single<MutableList<Station>> = Database.selectAll(tableName)
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
            .toList()

    override fun delete(): Single<Int> = Database.deleteAll(tableName).toSingle()

    override fun insert(element: Station): Single<Station> = Database.insert("INSERT INTO $tableName (name, stationuuid, url_resolved, " +
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
            Database.remove("delete from $tableName where stationuuid = :stationuuid")
                    .parameter("stationuuid", element.stationuuid)
                    .toSingle { element }

}