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

package online.hudacek.fxradio.storage

import io.reactivex.Single
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.model.Station
import org.nield.rxkotlinjdbc.execute
import org.nield.rxkotlinjdbc.insert
import org.nield.rxkotlinjdbc.select
import java.sql.Connection
import java.sql.DriverManager

/**
 * Database helper
 */
object Database {
    private val logger = KotlinLogging.logger {}

    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:${Config.Paths.dbPath}").apply {

        //Initial creation of tables
        execute("CREATE TABLE IF NOT EXISTS FAVOURITES (ID INTEGER PRIMARY KEY," +
                " stationuuid VARCHAR, name VARCHAR, " +
                " url_resolved VARCHAR, homepage VARCHAR," +
                " favicon VARCHAR, tags VARCHAR, country VARCHAR, " +
                " countrycode VARCHAR, state VARCHAR, language VARCHAR, codec VARCHAR, bitrate INTEGER" +
                " )")
                .toSingle()
                .subscribe({}, {
                    logger.error(it) { "There was an error creating database!" }
                })
    }

    fun cleanup(): Single<Int> = connection.execute("delete from favourites").toSingle()

    //get data about station from db and return latest info from API about it
    fun favourites(): Single<MutableList<Station>> =
            connection.select("SELECT * FROM FAVOURITES")
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


    fun isFavourite(station: Station): Single<Boolean> =
            connection.select("SELECT COUNT(*) FROM FAVOURITES WHERE stationuuid = :uuid")
                    .parameter("uuid", station.stationuuid)
                    .toSingle { it.getInt(1) > 0 }

    fun addFavourite(station: Station): Single<Boolean> =
            connection.insert("INSERT INTO FAVOURITES (name, stationuuid, url_resolved, " +
                    "homepage, country, countrycode, state, language, favicon, tags, codec, bitrate) " +
                    "VALUES (:name, :stationuuid, :url_resolved, :homepage, :country, :countrycode, :state, :language, :favicon, :tags, :codec, :bitrate )")
                    .parameter("name", station.name)
                    .parameter("stationuuid", station.stationuuid)
                    .parameter("url_resolved", station.url_resolved)
                    .parameter("homepage", station.homepage)
                    .parameter("country", station.country)
                    .parameter("countrycode", station.countrycode)
                    .parameter("state", station.state)
                    .parameter("language", station.language)
                    .parameter("favicon", station.favicon)
                    .parameter("tags", station.tags)
                    .parameter("codec", station.codec)
                    .parameter("bitrate", station.bitrate)
                    .toSingle { it.getInt(1) > 0 }

    fun removeFavourite(station: Station): Single<Boolean> =
            connection.insert("delete from favourites where stationuuid = :stationuuid")
                    .parameter("stationuuid", station.stationuuid)
                    .toSingle { it.getInt(1) > 0 }
}