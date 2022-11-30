package online.hudacek.fxradio.usecase.favourites

import io.reactivex.Single
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersSingle

class FavouritesClearUseCase : BaseUseCase<Unit, Single<Int>>() {

    override fun execute(input: Unit): Single<Int> = Tables.favourites.removeAll()
        .compose(applySchedulersSingle())
}