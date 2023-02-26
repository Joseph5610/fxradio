package online.hudacek.fxradio.persistence.database

import io.reactivex.rxjava3.core.Flowable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station

class FavouritesTable : StationTable("FAVOURITES") {

    override fun selectAll() =
        database.select("SELECT * FROM $tableName ORDER BY sorting_order ASC, id ASC;").asStationFlowable()

    override fun insert(element: Station): Flowable<Int> = database.update(
        "INSERT INTO $tableName (name, stationuuid, url_resolved, " +
                "homepage, country, countrycode, state, language, favicon, tags, codec, bitrate, sorting_order) " +
                "VALUES (:name, :stationuuid, :url_resolved, :homepage, :country, :countrycode, :state, :language, " +
                ":favicon, :tags, :codec, :bitrate, :sorting_order )"
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
        .parameter("sorting_order", Int.MAX_VALUE)
        .counts()

    fun updateOrder(station: Station, newOrderId: Int): Flowable<Int> =
        database.update("UPDATE $tableName SET sorting_order = ? WHERE stationuuid = ?;")
            .parameters(newOrderId, station.uuid)
            .counts()
}