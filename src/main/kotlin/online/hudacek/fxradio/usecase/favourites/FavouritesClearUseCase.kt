package online.hudacek.fxradio.usecase.favourites

import io.reactivex.rxjava3.core.Flowable
import online.hudacek.fxradio.persistence.database.Database
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersFlowable

class FavouritesClearUseCase : BaseUseCase<Unit, Flowable<Int>>() {

    override fun execute(input: Unit): Flowable<Int> = Database.favouritesDao.removeAll()
        .compose(applySchedulersFlowable())
        .onErrorComplete()
}