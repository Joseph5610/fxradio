package online.hudacek.fxradio.usecase.favourites

import io.reactivex.Flowable
import javafx.beans.property.ListProperty
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.usecase.BaseUseCase

class FavouriteUpdateUseCase : BaseUseCase<ListProperty<Station>, Flowable<Int>>() {

    override fun execute(input: ListProperty<Station>): Flowable<Int> = Flowable.fromIterable(input.withIndex())
        .flatMap { Tables.favourites.updateOrder(it.value, it.index) }
}
