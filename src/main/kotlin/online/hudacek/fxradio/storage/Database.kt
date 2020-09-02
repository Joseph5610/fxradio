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
import org.nield.rxkotlinjdbc.execute
import java.sql.Connection
import java.sql.DriverManager

/**
 * Database helper
 */
object Database {
    private val logger = KotlinLogging.logger {}

    val connection: Connection = DriverManager.getConnection("jdbc:sqlite:${Config.Paths.dbPath}").apply {

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
}