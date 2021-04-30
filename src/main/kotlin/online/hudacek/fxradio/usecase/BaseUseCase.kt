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

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import online.hudacek.fxradio.api.StationsApi
import tornadofx.Controller

/**
 * UseCase interface defines actions for interaction with data layers
 */
abstract class BaseUseCase<InputType, OutputType> : Controller() {

    val apiService by lazy { StationsApi.service }

    abstract fun execute(input: InputType): OutputType

    /**
     * Perform async calls on correct thread
     */
    protected fun <T> applySchedulers(): SingleTransformer<T, T>? = SingleTransformer {
        it.subscribeOn(Schedulers.io())
                .observeOnFx()
    }
}
