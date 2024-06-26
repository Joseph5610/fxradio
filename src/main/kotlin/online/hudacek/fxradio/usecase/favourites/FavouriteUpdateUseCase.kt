package online.hudacek.fxradio.usecase.favourites

import io.reactivex.rxjava3.core.Flowable
import javafx.beans.property.ListProperty
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.Database
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulersFlowable

class FavouriteUpdateUseCase : BaseUseCase<ListProperty<Station>, Flowable<Int>>() {

    override fun execute(input: ListProperty<Station>): Flowable<Int> = Flowable.fromIterable(input.withIndex())
        .flatMap { Database.favouritesDao.updateOrder(it.value, it.index) }
        .compose(applySchedulersFlowable())
}
