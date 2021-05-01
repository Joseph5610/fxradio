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

    init {
        stateObservableChanges()
                .subscribe({
                    onNewState(it)
                }, {
                    onNewStateError(it)
                })
    }

    fun stateObservableChanges(): Observable<State> = stateProperty
            .toObservableChangesNonNull()
            .filter { it.newVal != null }
            .map { it.newVal }

    /**
     * Called on every new state
     */
    protected open fun onNewState(newState: State) {
        logger.debug { "StateChange: $newState" }
    }

    /**
     * Called on state change error
     */
    protected open fun onNewStateError(error: Throwable) {
        logger.debug { "Error ${error.localizedMessage} when changing state" }
    }
}