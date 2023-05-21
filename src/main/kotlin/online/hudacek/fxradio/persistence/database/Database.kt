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

import io.reactivex.rxjava3.schedulers.Schedulers
import online.hudacek.fxradio.Config
import org.davidmoten.rxjava3.jdbc.Database
import org.davidmoten.rxjava3.jdbc.SelectBuilder
import org.davidmoten.rxjava3.jdbc.UpdateBuilder
import org.davidmoten.rxjava3.jdbc.pool.Pools
import org.flywaydb.core.Flyway
import java.util.concurrent.Executors

private const val DB_POOLS = 5

/**
 * Database helper class with useful methods to write/read from local sqlite.db
 */
open class Database(private val tableName: String) {

    fun selectAllQuery(): SelectBuilder = database.select("SELECT * FROM $tableName")

    fun removeAllQuery(): UpdateBuilder = database.update("DELETE FROM $tableName")

    companion object {

        private val dbUrl = "jdbc:sqlite:${Config.Paths.dbPath}"

        // Workaround for https://github.com/davidmoten/rxjava2-jdbc/issues/51
        private val executor = Executors.newFixedThreadPool(DB_POOLS)

        private val pools = Pools.nonBlocking()
            .url(dbUrl)
            .maxPoolSize(DB_POOLS)
            .scheduler(Schedulers.from(executor))
            .build()

        /**
         * Establishes connection to SQLite db with [dbUrl]
         * Performs create table operation for all tables in [Tables] object
         */
        internal val database: Database = Database.from(pools).also {
            /**
             * Apply flyway db migrations
             */
            Flyway.configure().dataSource(dbUrl, null, null).load().also {
                it.migrate()
            }
        }

        /**
         * Closes connection to DB
         */
        fun close() {
            database.close()
            executor.shutdownNow()
        }
    }
}

