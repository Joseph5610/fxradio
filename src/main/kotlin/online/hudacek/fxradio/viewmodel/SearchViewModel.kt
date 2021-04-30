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
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.usecase.SearchByNameUseCase
import online.hudacek.fxradio.usecase.SearchByTagUseCase
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import online.hudacek.fxradio.utils.value
import tornadofx.ItemViewModel
import tornadofx.property
import tornadofx.stringBinding

class Search(query: String = Properties.SearchQuery.value(""),
             useTagSearch: Boolean = false) {
    var query: String by property(query)
    var searchByTag: Boolean by property(useTagSearch)
}

class SearchViewModel : ItemViewModel<Search>(Search()) {

    private val searchByTagUseCase: SearchByTagUseCase by inject()
    private val searchByNameUseCase: SearchByNameUseCase by inject()

    val searchByTagProperty = bind(Search::searchByTag) as BooleanProperty

    //Internal only, contains unedited search query
    val bindQueryProperty = bind(Search::query) as StringProperty

    //Search query is limited to 50 chars and trimmed to reduce requests to API
    val queryProperty = bindQueryProperty.stringBinding {
        if (it != null) {
            if (it.length > 50) it.substring(0, 50).trim() else it.trim()
        } else ""
    }

    val queryObservable: Observable<String> = queryProperty
            .toObservableChangesNonNull()
            .map { it.newVal }

    fun searchByTag() = searchByTagUseCase.execute(queryProperty)

    fun searchByName() = searchByNameUseCase.execute(queryProperty)

    override fun onCommit() = Property(Properties.SearchQuery).save(bindQueryProperty.value)
}