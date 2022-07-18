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

package online.hudacek.fxradio.viewmodel

import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import io.reactivex.Observable
import mu.KotlinLogging
import tornadofx.objectProperty

private val logger = KotlinLogging.logger {}

abstract class BaseStateViewModel<Item : Any, State : Any>(initialItem: Item? = null,
                                                           initialState: State? = null) :
        BaseViewModel<Item>(initialItem = initialItem) {

    val stateProperty = objectProperty(initialState)

    val stateObservableChanges: Observable<State> = stateProperty
            .toObservableChangesNonNull()
            .filter { it.newVal != null }
            .map { it.newVal }

    init {
        stateObservableChanges.subscribe(::onNewState, ::onError)
    }

    /**
     * Called on every new state
     */
    protected open fun onNewState(newState: State) {
        logger.debug { "StateChange: $newState" }
    }


    /**
     * Called on every new state
     */
    protected open fun onError(throwable: Throwable) {
        logger.error(throwable) { "Exception when changing state" }
    }
}