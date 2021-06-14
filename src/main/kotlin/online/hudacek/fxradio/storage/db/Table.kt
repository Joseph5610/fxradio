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

/**
 * Basic interface for common table operations
 */
interface Table<T> {
    /**
     *  Name of the table in SQLite DB
     */
    val tableName: String

    fun selectAll(): Observable<T>
    fun removeAll(): Single<Int>
    fun insert(element: T): Single<T>
    fun remove(element: T): Single<T>
}



