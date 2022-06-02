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

import io.reactivex.disposables.Disposable
import mu.KotlinLogging
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.viewmodel.Favourites
import online.hudacek.fxradio.viewmodel.FavouritesViewModel

private val logger = KotlinLogging.logger {}

/**
 * Removes favourites DB and resets viewmodel
 */
class FavouritesClearUseCase : BaseUseCase<FavouritesViewModel, Disposable>() {

    override fun execute(input: FavouritesViewModel): Disposable = Tables.favourites.removeAll()
            .subscribe({
                input.item = Favourites()
            }, {
                logger.error(it) { "Exception when performing DB cleanup!" }
            })
}