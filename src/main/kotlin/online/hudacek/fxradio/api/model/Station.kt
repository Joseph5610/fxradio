package online.hudacek.fxradio.api.model

import io.reactivex.Single
import online.hudacek.fxradio.storage.Database
import org.nield.rxkotlinjdbc.insert
import org.nield.rxkotlinjdbc.select

/**
 * Stations json structure
 */
data class Station(
        val stationuuid: String,
        val name: String,
        val url_resolved: String?,
        val homepage: String,
        var favicon: String?,
        val tags: String = "",
        val country: String = "",
        val countrycode: String = "",
        val state: String = "",
        val language: String = "",
        val codec: String = "",
        val bitrate: Int = 0,
        var votes: Int = 0
) {

    val isFavourite: Single<Boolean>
        get() =
            Database.connection.select("SELECT COUNT(*) FROM FAVOURITES WHERE stationuuid = :uuid")
                    .parameter("uuid", stationuuid)
                    .toSingle { it.getInt(1) > 0 }


    fun addFavourite(): Single<Boolean> =
            Database.connection.insert("INSERT INTO FAVOURITES (name, stationuuid, url_resolved, " +
                    "homepage, country, countrycode, state, language, favicon, tags, codec, bitrate) " +
                    "VALUES (:name, :stationuuid, :url_resolved, :homepage, :country, :countrycode, :state, :language, :favicon, :tags, :codec, :bitrate )")
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
                    .parameter("codec", codec)
                    .parameter("bitrate", bitrate)
                    .toSingle { it.getInt(1) > 0 }

    fun removeFavourite(): Single<Boolean> =
            Database.connection.insert("delete from favourites where stationuuid = :stationuuid")
                    .parameter("stationuuid", stationuuid)
                    .toSingle { it.getInt(1) > 0 }

    fun isValidStation() = stationuuid != "0"

    fun isInvalidImage() = favicon.isNullOrEmpty()

    override fun equals(other: Any?): Boolean {
        return if (other is Station) {
            this.stationuuid == other.stationuuid
        } else super.equals(other)
    }

    override fun hashCode() = super.hashCode()

    companion object {
        fun stub() = Station(
                "0",
                "Not playing",
                "none",
                "",
                null)

        //get data about station from db and return latest info from API about it
        fun favourites(): Single<MutableList<Station>> =
                Database.connection.select("SELECT * FROM FAVOURITES")
                        .toObservable {
                            Station(it.getString("stationuuid"),
                                    it.getString("name"),
                                    it.getString("url_resolved"),
                                    it.getString("homepage"),
                                    it.getString("favicon"),
                                    it.getString("tags"),
                                    it.getString("country"),
                                    it.getString("countrycode"),
                                    it.getString("state"),
                                    it.getString("language"),
                                    it.getString("codec"),
                                    it.getInt("bitrate"))
                        }
                        .toList()


    }
}

