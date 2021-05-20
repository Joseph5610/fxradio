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

import io.reactivex.Observable
import io.reactivex.Single
import mu.KotlinLogging
import online.hudacek.fxradio.api.stations.model.Station

private val logger = KotlinLogging.logger {}

/**
 * Common operations on database of stations with different tables (e.g History, Favourites ..)
 */
class StationTable(override val tableName: String) : Table<Station>, Database(tableName) {

    override val createTableSql = "CREATE TABLE IF NOT EXISTS $tableName (ID INTEGER PRIMARY KEY," +
            " stationuuid VARCHAR, name VARCHAR, " +
            " url_resolved VARCHAR, homepage VARCHAR," +
            " favicon VARCHAR, tags VARCHAR, country VARCHAR, " +
            " countrycode VARCHAR, state VARCHAR, language VARCHAR, codec VARCHAR, bitrate INTEGER" +
            " )"

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
            .doOnError { logger.error(it) { "Exception when retrieving data from $tableName" } }

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