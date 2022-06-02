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

import online.hudacek.fxradio.Config
import org.flywaydb.core.Flyway
import org.nield.rxkotlinjdbc.execute
import org.nield.rxkotlinjdbc.insert
import org.nield.rxkotlinjdbc.select
import java.sql.Connection
import java.sql.DriverManager

/**
 * Database helper class with useful methods to write/read from local sqlite.db
 */
abstract class Database(open val tableName: String) {

    fun selectAllQuery() = connection.select("SELECT * FROM $tableName")

    fun removeAllQuery() = connection.execute("DELETE FROM $tableName")

    fun insertQuery(query: String) = connection.insert(query)

    fun removeQuery(query: String) = connection.execute(query)

    private companion object {
        private val dbUrl = "jdbc:sqlite:${Config.Paths.dbPath}"

        /**
         * Establishes connection to SQLite db with [dbUrl]
         * Performs create table operation for all tables in [Tables] object
         */
        private val connection: Connection = DriverManager.getConnection(dbUrl).apply {
            /**
             * Apply flyway db migrations
             */
            val flyway = Flyway.configure().dataSource(dbUrl, null, null).load()
            flyway.migrate()
        }
    }
}

