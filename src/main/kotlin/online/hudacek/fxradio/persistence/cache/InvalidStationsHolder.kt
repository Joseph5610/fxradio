package online.hudacek.fxradio.persistence.cache

import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import tornadofx.observableListOf

/**
 * Holds list of stations that have invalid station logo
 * Purpose is to not try to download it each time the station is visible in the grid,
 * instead it is simply ignored until next start of the app as the list is not persistent
 */
object InvalidStationsHolder {
    val invalidLogoStations = observableListOf<Station>()

    fun Station.setInvalidLogo() {
        if (!invalidLogoStations.contains(this)) {
            invalidLogoStations.add(this)
        }
    }

    fun Station.hasInvalidLogo() = invalidLogoStations.contains(this)
}