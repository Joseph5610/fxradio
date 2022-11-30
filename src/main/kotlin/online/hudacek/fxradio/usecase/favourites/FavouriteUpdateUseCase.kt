package online.hudacek.fxradio.usecase.favourites

import io.reactivex.Observable
import javafx.beans.property.ListProperty
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.Tables
import online.hudacek.fxradio.usecase.BaseUseCase
import online.hudacek.fxradio.util.applySchedulers

class FavouriteUpdateUseCase : BaseUseCase<ListProperty<Station>, Observable<Station>>() {

    override fun execute(input: ListProperty<Station>): Observable<Station> = Observable.fromIterable(input.withIndex())
        .compose(applySchedulers())
        .flatMapSingle { Tables.favourites.updateOrder(it.value, it.index) }
}
