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

package online.hudacek.broadcastsfx

import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import org.nield.rxkotlinjdbc.execute
import java.sql.DriverManager

/**
 * Database helper
 */
internal val db = DriverManager.getConnection("jdbc:sqlite:${Config.Paths.db}").apply {

    //Initial creation of tables
    execute("CREATE TABLE IF NOT EXISTS FAVOURITES (ID INTEGER PRIMARY KEY," +
            " stationuuid VARCHAR, name VARCHAR, " +
            " url_resolved VARCHAR, homepage VARCHAR," +
            " favicon VARCHAR, tags VARCHAR, country VARCHAR, countrycode VARCHAR, state VARCHAR, language VARCHAR" +
            " )")
            .toSingle()
            .subscribe()

    execute("CREATE TABLE IF NOT EXISTS HISTORY (ID INTEGER PRIMARY KEY," +
            " stationuuid VARCHAR, name VARCHAR, " +
            " url_resolved VARCHAR, homepage VARCHAR," +
            " favicon VARCHAR, tags VARCHAR, country VARCHAR, countrycode VARCHAR, state VARCHAR, language VARCHAR" +
            " )")
            .toSingle()
            .subscribe()
}

/**
 * Workaround for [SQLite locking error](https://github.com/davidmoten/rxjava-jdbc#note-for-sqlite-users).
 * Collecting items and then emitting them again allows query
 * to close and open connection for other queries
 */
fun <T : Any> Observable<T>.flatCollect(): Observable<T> = toList().flatMapObservable { it.toObservable() }