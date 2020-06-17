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

package online.hudacek.broadcastsfx.controllers

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.events.LibraryRefreshEvent
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.model.Library
import online.hudacek.broadcastsfx.model.rest.CountriesBody
import online.hudacek.broadcastsfx.views.LibraryView
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class LibraryController : Controller() {

    private val libraryView: LibraryView by inject()

    private val stationsApi: StationsApi
        get() = StationsApi.client

    val libraryItems by lazy {
        observableListOf(
                Library(LibraryType.TopStations, FontAwesome.Glyph.THUMBS_UP),
                Library(LibraryType.Favourites, FontAwesome.Glyph.STAR),
                Library(LibraryType.History, FontAwesome.Glyph.HISTORY)
        )
    }

    fun getCountries(): Disposable = stationsApi
            .getCountries(CountriesBody())
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({
                libraryView.showCountries(it.toObservable())
            }, {
                libraryView.showError()
            })

    fun loadLibrary(libraryType: LibraryType, param: String = "") = fire(LibraryRefreshEvent(libraryType, param))
}