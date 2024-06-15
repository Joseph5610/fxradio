package online.hudacek.fxradio.persistence.database

import io.reactivex.rxjava3.schedulers.Schedulers
import online.hudacek.fxradio.Config
import org.davidmoten.rxjava3.jdbc.Database
import org.davidmoten.rxjava3.jdbc.pool.Pools
import org.flywaydb.core.Flyway
import java.util.concurrent.Executors

private const val DB_POOLS = 5

class ConnectionManager(private val dbUrl: String) {

    // Workaround for https://github.com/davidmoten/rxjava2-jdbc/issues/51
    private val executor = Executors.newFixedThreadPool(DB_POOLS)

    private val pools = Pools.nonBlocking()
        .url(dbUrl)
        .maxPoolSize(DB_POOLS)
        .scheduler(Schedulers.from(executor))
        .build()

    /**
     * Establishes connection to SQLite db with [dbUrl]
     * Performs flyway migrations for the given database
     */
    val database: Database by lazy {
       Database.from(pools).also {
            Flyway.configure().dataSource(dbUrl, null, null).load().also {
                it.migrate()
            }
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