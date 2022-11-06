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

import com.github.thomasnield.rxkotlinfx.toObservable
import io.reactivex.Observable
import mu.KotlinLogging
import tornadofx.objectProperty

private val logger = KotlinLogging.logger {}

abstract class BaseStateViewModel<Item : Any, State : Any>(initialItem: Item, initialState: State) :
    BaseViewModel<Item>(initialItem = initialItem) {

    val stateProperty by lazy { objectProperty(initialState) }

    val stateObservable: Observable<State> = stateProperty.toObservable(initialState)

    init {
        stateObservable.subscribe(::onNewState, ::onError)
    }

    /**
     * Called on every new state
     */
    protected open fun onNewState(newState: State) {
        logger.trace { "State Change: $newState" }
    }


    /**
     * Called on every new state
     */
    protected open fun onError(throwable: Throwable) {
        logger.error(throwable) { "Exception when changing state" }
    }
}
