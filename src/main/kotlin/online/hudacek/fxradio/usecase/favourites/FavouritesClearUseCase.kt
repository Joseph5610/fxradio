package online.hudacek.fxradio.usecase.favourites

import io.reactivex.Flowable
import io.reactivex.Single
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersFlowable
import online.hudacek.fxradio.util.applySchedulersSingle

class FavouritesClearUseCase : BaseUseCase<Unit, Flowable<Int>>() {

    override fun execute(input: Unit): Flowable<Int> = Tables.favourites.removeAll()
        .compose(applySchedulersFlowable())
}