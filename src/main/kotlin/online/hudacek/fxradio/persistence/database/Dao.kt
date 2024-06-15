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
import org.davidmoten.rxjava3.jdbc.Database
import org.davidmoten.rxjava3.jdbc.SelectBuilder
import org.davidmoten.rxjava3.jdbc.UpdateBuilder

/**
 * Basic interface for common table operations
 */
interface Dao<In : Any, Out: Any> {

    val database: Database

    /**
     *  Name of the table in SQLite DB
     */
    val tableName: String

    fun selectAll(): Flowable<Out>

    fun removeAll(): Flowable<Int>

    fun insert(element: In): Flowable<Int>

    fun remove(element: In): Flowable<Int>

    fun selectAllQuery(): SelectBuilder = database.select("SELECT * FROM $tableName")

    fun removeAllQuery(): UpdateBuilder = database.update("DELETE FROM $tableName")
}



