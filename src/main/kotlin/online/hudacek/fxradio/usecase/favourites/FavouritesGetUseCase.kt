package online.hudacek.fxradio.usecase.favourites

import io.reactivex.Observable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulers

class FavouritesGetUseCase : BaseUseCase<Unit, Observable<Station>>() {

    override fun execute(input: Unit): Observable<Station> = Tables.favourites.selectAll()
        .compose(applySchedulers())
}
