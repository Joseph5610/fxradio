package online.hudacek.fxradio.usecase.favourites

import io.reactivex.rxjava3.core.Flowable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersFlowable

class FavouritesGetUseCase : BaseUseCase<Unit, Flowable<Station>>() {

    override fun execute(input: Unit): Flowable<Station> = Tables.favourites.selectAll()
        .compose(applySchedulersFlowable())
}
