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
import online.hudacek.fxradio.api.model.Country

private val logger = KotlinLogging.logger {}

class PinnedCountriesTable(override val tableName: String = "PINNED") : TableOperations<Country>, Database(tableName) {

    override val createTableSql = "CREATE TABLE IF NOT EXISTS $tableName (ID INTEGER PRIMARY KEY," +
            " name VARCHAR " +
            " )"

    override fun selectAll(): Observable<Country> = selectAllQuery()
            .toObservable {
                //We do not store the count of stations for given pinned country
                //so the returned object will have count set to 0 and the number is not displayed in UI
                Country(it.getString("name"), 0)
            }
            .doOnError { logger.error(it) { "Error when getting $tableName" } }

    override fun removeAll(): Single<Int> = removeAllQuery().toSingle()

    override fun insert(element: Country): Single<Country> = insertQuery("INSERT INTO $tableName (name)  " +
            "VALUES (:name)")
            .parameter("name", element.name)
            .toSingle { element }

    override fun remove(element: Country): Single<Country> = removeQuery("DELETE FROM $tableName WHERE name = ?")
            .parameter(element.name)
            .toSingle()
            .map { element }
}
