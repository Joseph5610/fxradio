package online.hudacek.fxradio.usecase.favourites

import io.reactivex.rxjava3.core.Flowable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.Database
import online.hudacek.fxradio.persistence.database.entity.StationEntity
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersFlowable

class FavouritesGetUseCase : BaseUseCase<Unit, Flowable<Station>>() {

    override fun execute(input: Unit): Flowable<Station> = Database.favouritesDao.selectAll()
        .map { it.convertToStation() }
        .compose(applySchedulersFlowable())
}

private fun StationEntity.convertToStation() = Station(
    uuid(),
    name(),
    urlResolved(),
    homepage(),
    favicon(),
    tags() ?: "",
    country() ?: "",
    countryCode() ?: "",
    state() ?: "",
    language() ?: "",
    codec() ?: "",
    bitrate(),
    hasExtendedInfo()
)
