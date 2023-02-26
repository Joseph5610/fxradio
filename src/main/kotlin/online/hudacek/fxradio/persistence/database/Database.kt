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

import io.reactivex.schedulers.Schedulers
import online.hudacek.fxradio.Config
import org.davidmoten.rx.jdbc.Database
import org.davidmoten.rx.jdbc.SelectBuilder
import org.davidmoten.rx.jdbc.UpdateBuilder
import org.davidmoten.rx.jdbc.pool.Pools
import org.flywaydb.core.Flyway
import java.util.concurrent.Executors

private val DB_URL = "jdbc:sqlite:${Config.Paths.dbPath}"
private const val DB_POOLS = 5

/**
 * Database helper class with useful methods to write/read from local sqlite.db
 */
open class Database(private val tableName: String) {

    fun selectAllQuery(): SelectBuilder = database.select("SELECT * FROM $tableName")

    fun removeAllQuery(): UpdateBuilder = database.update("DELETE FROM $tableName")


    companion object {
        // Workaround for https://github.com/davidmoten/rxjava2-jdbc/issues/51
        private val executor = Executors.newFixedThreadPool(DB_POOLS)

        private val pools = Pools.nonBlocking()
            .url(DB_URL)
            .maxPoolSize(DB_POOLS)
            .scheduler(Schedulers.from(executor))
            .build()

        /**
         * Establishes connection to SQLite db with [DB_URL]
         * Performs create table operation for all tables in [Tables] object
         */
        internal val database: Database = Database.from(pools).also {
            /**
             * Apply flyway db migrations
             */
            Flyway.configure().dataSource(DB_URL, null, null).load().also {
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

