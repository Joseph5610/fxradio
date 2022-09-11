/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.usecase

import io.reactivex.Observable
import io.reactivex.Single
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.stations.model.Station
import online.hudacek.fxradio.data.db.Tables

private val logger = KotlinLogging.logger {}


class FavouriteAddUseCase : BaseUseCase<Station, Single<Station>>() {

    override fun execute(input: Station): Single<Station> = Tables.favourites.insert(input)
            .doOnError { logger.error(it) { "Exception when adding $input!" } }
}

class FavouriteSetUseCase : BaseUseCase<Unit, Observable<Station>>() {

    override fun execute(input: Unit): Observable<Station> = Tables.favourites.selectAll()
            .doOnError { logger.error(it) { "Exception when setting favourite stations!" } }
}

class FavouritesClearUseCase : BaseUseCase<Unit, Single<Int>>() {

    override fun execute(input: Unit): Single<Int> = Tables.favourites.removeAll()
            .doOnError { logger.error(it) { "Exception when clearing favourite stations!" } }
}

class FavouriteRemoveUseCase : BaseUseCase<Station, Single<Station>>() {

    override fun execute(input: Station): Single<Station> = Tables.favourites.remove(input)
            .doOnError { logger.error(it) { "Exception when removing favourite $input!" } }
}
