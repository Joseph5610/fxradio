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
import online.hudacek.fxradio.apiclient.stations.model.Country

class PinnedCountriesTable(override val tableName: String = "PINNED") : Table<Country>, Database(tableName) {

    override fun selectAll(): Observable<Country> = selectAllQuery()
            .toObservable {
                //We do not store the count of stations for given pinned country
                //so the returned object will have count set to 0 and the number is not displayed in UI
                Country(it.getString("name"), it.getString("iso3"), 0)
            }

    override fun removeAll(): Single<Int> = removeAllQuery().toSingle()

    override fun insert(element: Country): Single<Country> = insertQuery("INSERT INTO $tableName (name, iso3)  " +
            "VALUES (:name, :iso3)")
            .parameter("name", element.name)
            .parameter("iso3", element.iso_3166_1)
            .toSingle { element }

    override fun remove(element: Country): Single<Country> = removeQuery("DELETE FROM $tableName WHERE name = ?")
            .parameter(element.name)
            .toSingle()
            .map { element }
}
