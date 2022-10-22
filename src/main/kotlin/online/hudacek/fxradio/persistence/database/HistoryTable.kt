package online.hudacek.fxradio.persistence.database

import io.reactivex.Observable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import org.nield.rxkotlinjdbc.select

class HistoryTable : StationTable("HISTORY") {

    override fun selectAll(): Observable<Station> =
        connection.select("SELECT * FROM $tableName ORDER BY id DESC;").toStationObservable()
}