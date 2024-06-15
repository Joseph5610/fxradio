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

import io.reactivex.rxjava3.core.Flowable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.persistence.database.entity.CountryEntity
import org.davidmoten.rxjava3.jdbc.Database

class PinnedCountriesDao(override val database: Database) : Dao<Country, CountryEntity> {

    override val tableName = "PINNED"

    override fun selectAll(): Flowable<CountryEntity> = database.select(CountryEntity::class.java).get()

    override fun removeAll(): Flowable<Int> = removeAllQuery().counts()

    override fun insert(element: Country): Flowable<Int> =
        database.update("INSERT INTO $tableName (name, iso3) VALUES (:name, :iso3)")
            .parameter("name", element.name)
            .parameter("iso3", element.iso3166)
            .counts()

    override fun remove(element: Country): Flowable<Int> = database.update("DELETE FROM $tableName WHERE name = ?")
        .parameter(element.name)
        .counts()
}
