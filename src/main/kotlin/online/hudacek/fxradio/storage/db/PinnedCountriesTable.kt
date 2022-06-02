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
