package online.hudacek.fxradio.api.model

import io.reactivex.Single
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.storage.Database

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
        get() = Database.isFavourite(this)

    fun addFavourite(): Single<Boolean> = Database.addFavourite(this)

    fun removeFavourite(): Single<Boolean> = Database.removeFavourite(this)

    fun isValid() = stationuuid != "0"

    fun isInvalidImage() = favicon.isNullOrEmpty()

    override fun equals(other: Any?): Boolean {
        return if (other is Station) {
            this.stationuuid == other.stationuuid
        } else super.equals(other)
    }

    override fun hashCode() = super.hashCode()

    companion object {
        val stub by lazy {
            Station("0", "No stations found", null,
                    FxRadio.appUrl, null)
        }
    }
}

