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

package online.hudacek.fxradio.controllers

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import online.hudacek.fxradio.StationsApi
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.model.rest.CountriesBody
import online.hudacek.fxradio.views.LibraryView
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

data class LibraryItem(val type: LibraryType, val graphic: FontAwesome.Glyph)

class LibraryController : Controller() {

    private val libraryView: LibraryView by inject()

    private val stationsApi: StationsApi
        get() = StationsApi.client

    //Default items shown in library ListView
    val libraryItems by lazy {
        observableListOf(
                LibraryItem(LibraryType.TopStations, FontAwesome.Glyph.TROPHY),
                LibraryItem(LibraryType.Favourites, FontAwesome.Glyph.STAR),
                LibraryItem(LibraryType.History, FontAwesome.Glyph.HISTORY)
        )
    }

    fun getCountries(): Disposable = stationsApi
            .getCountries(CountriesBody())
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ response ->
                //Ignore invalid states
                val result = response.filter {
                    it.name.length > 1 && !it.name.contains(".")
                }.asObservable()

                libraryView.showCountries(result)
            }, {
                libraryView.showError()
            })
}