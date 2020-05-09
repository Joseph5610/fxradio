package online.hudacek.broadcastsfx

import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import org.nield.rxkotlinjdbc.execute
import java.sql.Connection
import java.sql.DriverManager

val db: Connection = DriverManager.getConnection("jdbc:sqlite:${Config.dbPath}").apply {
    execute("CREATE TABLE IF NOT EXISTS FAVOURITES (ID INTEGER PRIMARY KEY," +
            " changeuuid VARCHAR, stationuuid VARCHAR, name VARCHAR, " +
            "url VARCHAR, url_resolved VARCHAR, homepage VARCHAR," +
            " favicon VARCHAR, tags VARCHAR, country VARCHAR, countrycode VARCHAR, state VARCHAR, language VARCHAR" +
            " votes INT(10),lastchangetime VARCHAR, codec VARCHAR, bitrate INT(10), hls INT(10) )")
            .toSingle()
            .subscribe()
}

/**
 * Workaround for [SQLite locking error](https://github.com/davidmoten/rxjava-jdbc#note-for-sqlite-users).
 * Collecting items and then emitting them again allows query
 * to close and open connection for other queries
 */
fun <T : Any> Observable<T>.flatCollect(): Observable<T> = toList().flatMapObservable { it.toObservable() }