package online.hudacek.fxradio.persistence.database

import io.reactivex.Observable
import io.reactivex.Single
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import org.nield.rxkotlinjdbc.select

class HistoryTable : StationTable("HISTORY") {
    override fun selectAll(): Observable<Station> =
        connection.select("SELECT * FROM $tableName ORDER BY id DESC;").toStationObservable()

    override fun insert(element: Station): Single<Station> = insertQuery(
        "INSERT INTO $tableName (name, stationuuid, url_resolved, " +
                "homepage, country, countrycode, state, language, favicon, tags, codec, bitrate) " +
                "VALUES (:name, :stationuuid, :url_resolved, :homepage, :country, :countrycode, :state, :language, :favicon, :tags, :codec, :bitrate )"
    )
        .parameter("name", element.name)
        .parameter("stationuuid", element.stationuuid)
        .parameter("url_resolved", element.url_resolved)
        .parameter("homepage", element.homepage)
        .parameter("country", element.country)
        .parameter("countrycode", element.countrycode)
        .parameter("state", element.state)
        .parameter("language", element.language)
        .parameter("favicon", element.favicon)
        .parameter("tags", element.tags)
        .parameter("codec", element.codec)
        .parameter("bitrate", element.bitrate)
        .toSingle { element }
}