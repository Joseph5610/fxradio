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
import online.hudacek.fxradio.api.model.Country

class PinnedTable : TableOperations<Country> {

    override val tableName = "PINNED"

    override val createTableSql = "CREATE TABLE IF NOT EXISTS $tableName (ID INTEGER PRIMARY KEY," +
            " name VARCHAR " +
            " )"

    override fun select(): Single<MutableList<Country>> = Database.selectAll(tableName)
            .toObservable {
                Country(it.getString("name"), 0)
            }
            .toList()

    override fun delete(): Single<Int> = Database.deleteAll(tableName).toSingle()

    override fun insert(element: Country): Single<Country> = Database.insert("INSERT INTO $tableName (name)  " +
            "VALUES (:name)")
            .parameter("name", element.name)
            .toSingle { element }

    override fun remove(element: Country): Single<Country> = Database.remove("delete from $tableName where name = :name")
            .parameter("name", element.name)
            .toSingle { element }
}
