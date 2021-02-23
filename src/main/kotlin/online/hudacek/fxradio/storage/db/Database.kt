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

import mu.KotlinLogging
import online.hudacek.fxradio.Config
import org.nield.rxkotlinjdbc.execute
import org.nield.rxkotlinjdbc.insert
import org.nield.rxkotlinjdbc.select
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.full.declaredMemberProperties

/**
 * Database helper class contains useful methods to write/read from local sqlite.db which stores
 * Favourite stations and listening history
 */
private val logger = KotlinLogging.logger {}

object Database {

    private val dbUrl = "jdbc:sqlite:${Config.Paths.dbPath}"

    private val connection: Connection = DriverManager.getConnection(dbUrl).apply {

        //Get trough all tables declared in Tables object and
        //execute create table statements accordingly
        Tables::class.declaredMemberProperties.forEach {
            val member = it.get(Tables) as TableOperations<*> //Get instance of member

            execute(member.createTableSql)
                    .toSingle()
                    .subscribe({
                        logger.info { "Create table for ${member.tableName} returned result $it" }
                    }, { e ->
                        logger.error(e) { "There was an error creating ${member.tableName} table!" }
                    })
        }
    }

    fun selectAll(tableName: String) = connection.select("SELECT * FROM $tableName")

    fun deleteAll(tableName: String) = connection.execute("delete from $tableName")

    fun insert(query: String) = connection.insert(query)

    fun remove(query: String) = connection.insert(query)

}

