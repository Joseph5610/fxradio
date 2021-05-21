/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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