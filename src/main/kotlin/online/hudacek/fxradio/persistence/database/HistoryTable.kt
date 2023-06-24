package online.hudacek.fxradio.persistence.database

import io.reactivex.rxjava3.core.Flowable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station

class HistoryTable : StationTable(tableName = "HISTORY") {
    override fun selectAll(): Flowable<Station> =
        database.select("SELECT * FROM $tableName ORDER BY id DESC;").asStationFlowable()

    override fun insert(element: Station): Flowable<Int> = database.update(
        "INSERT INTO $tableName (name, stationuuid, url_resolved, " +
                "homepage, country, countrycode, state, language, favicon, tags, codec, bitrate, has_extended_info) " +
                "VALUES (:name, :stationuuid, :url_resolved, :homepage, :country, :countrycode, :state, :language," +
                " :favicon, :tags, :codec, :bitrate, :has_extended_info )"
    )
        .parameter("name", element.name)
        .parameter("stationuuid", element.uuid)
        .parameter("url_resolved", element.urlResolved)
        .parameter("homepage", element.homepage)
        .parameter("country", element.country)
        .parameter("countrycode", element.countryCode)
        .parameter("state", element.state)
        .parameter("language", element.language)
        .parameter("favicon", element.favicon)
        .parameter("tags", element.tags)
        .parameter("codec", element.codec)
        .parameter("bitrate", element.bitrate)
        .parameter("has_extended_info", element.hasExtendedInfo)
        .counts()
}
