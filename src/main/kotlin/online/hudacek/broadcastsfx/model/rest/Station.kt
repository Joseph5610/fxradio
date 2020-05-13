package online.hudacek.broadcastsfx.model.rest

import io.reactivex.Observable
import io.reactivex.Single
import online.hudacek.broadcastsfx.db
import org.nield.rxkotlinjdbc.insert
import org.nield.rxkotlinjdbc.select

/**
 * Stations json structure
 */
data class Station(
        val changeuuid: String,
        val stationuuid: String,
        val name: String,
        val url: String,
        val url_resolved: String?,
        val homepage: String,
        var favicon: String?,
        val tags: String,
        val country: String,
        val countrycode: String,
        val state: String,
        val language: String,
        val votes: Int = 0,
        val lastchangetime: String,
        val codec: String,
        val bitrate: Int = 0,
        val hls: Int = 0,
        val lastcheckok: Int = 0,
        val lastchecktime: String,
        val lastcheckoktime: String,
        val lastlocalchecktime: String,
        val clicktimestamp: String,
        val clickcount: Int = 0,
        val clicktrend: Int = 0
) {

    val isFavourite: Single<Int>
        get() =
            db.select("SELECT COUNT(*) FROM FAVOURITES WHERE stationuuid = :uuid")
                    .parameter("uuid", stationuuid)
                    .toSingle { it.getInt(1) }

    fun addFavourite(): Single<Int> =
            db.insert("INSERT INTO FAVOURITES (name, stationuuid, url_resolved, " +
                    "homepage, country, countrycode, state, language, favicon, tags) " +
                    "VALUES (:name, :stationuuid, :url_resolved, :homepage, :country, :countrycode, :state, :language, :favicon, :tags )")
                    .parameter("name", name)
                    .parameter("stationuuid", stationuuid)
                    .parameter("url_resolved", url_resolved)
                    .parameter("homepage", homepage)
                    .parameter("country", country)
                    .parameter("countrycode", countrycode)
                    .parameter("state", state)
                    .parameter("language", language)
                    .parameter("favicon", favicon)
                    .parameter("tags", tags)
                    .toSingle { it.getInt(1) }

    fun isValidStation() = stationuuid != "0"

    fun isInvalidImage() = favicon.isNullOrEmpty() || favicon!!.contains(".ico")

    override fun equals(other: Any?): Boolean {
        return if (other is Station) {
            this.stationuuid == other.stationuuid
        } else super.equals(other)
    }

    override fun hashCode() = super.hashCode()

    companion object {
        fun stub() = Station(
                "0",
                "0",
                "Not playing",
                "none", null,
                "", null,
                "", "", "",
                "", "", 0, "",
                "", 0, 0, 0, "",
                "", "", "")

        //get data about station from db and return latest info from API about it
        fun favourites(): Observable<Station> = db
                .select("SELECT * FROM FAVOURITES")
                .toObservable {
                    Station("0",
                            it.getString("stationuuid"),
                            it.getString("name"),
                            it.getString("url_resolved"),
                            it.getString("url_resolved"),
                            it.getString("homepage"),
                            it.getString("favicon"),
                            it.getString("tags"),
                            it.getString("country"),
                            it.getString("countrycode"),
                            it.getString("state"),
                            it.getString("language"),
                            0, "",
                            "", 0, 0, 0, "",
                            "", "", "")
                }

    }
}

