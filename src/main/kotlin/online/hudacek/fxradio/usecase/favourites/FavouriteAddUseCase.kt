package online.hudacek.fxradio.usecase.favourites

import io.reactivex.rxjava3.core.Flowable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.Database
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersFlowable

class FavouriteAddUseCase : BaseUseCase<Station, Flowable<Int>>() {

    override fun execute(input: Station): Flowable<Int> = Database.favouritesDao.insert(input)
        .compose(applySchedulersFlowable())
}
