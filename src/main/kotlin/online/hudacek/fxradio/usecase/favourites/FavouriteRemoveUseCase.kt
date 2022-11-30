package online.hudacek.fxradio.usecase.favourites

import io.reactivex.Single
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersSingle

class FavouriteRemoveUseCase : BaseUseCase<Station, Single<Station>>() {

    override fun execute(input: Station): Single<Station> = Tables.favourites.remove(input)
        .compose(applySchedulersSingle())
}
