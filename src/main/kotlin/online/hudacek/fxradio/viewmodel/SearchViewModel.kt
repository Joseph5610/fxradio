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

import com.github.thomasnield.rxkotlinfx.toBinding
import com.github.thomasnield.rxkotlinfx.toObservable
import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import io.reactivex.Observable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.usecase.SearchUseCase
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import online.hudacek.fxradio.utils.value
import tornadofx.property

class Search(query: String = Properties.SearchQuery.value(""),
             useTagSearch: Boolean = false) {
    var query: String by property(query)
    var searchByTag: Boolean by property(useTagSearch)
}

class SearchViewModel : BaseViewModel<Search>(Search()) {

    private val searchUseCase: SearchUseCase by inject()

    val searchByTagProperty = bind(Search::searchByTag) as BooleanProperty

    //Internal only, contains unedited search query
    val bindQueryProperty = bind(Search::query) as StringProperty

    //Search query is limited to 50 chars and trimmed to reduce requests to API
    val queryChanges: Observable<String> = bindQueryProperty
            .toObservableChangesNonNull()
            .map { it.newVal }
            .map { if (it.length > 50) it.substring(0, 50).trim() else it.trim() }

    val queryBinding = bindQueryProperty.toObservable()
            .map { if (it.length > 50) it.substring(0, 50).trim() else it.trim() }
            .toBinding()

    fun search() = searchUseCase.execute(searchByTagProperty.value to queryBinding.value)

    override fun onCommit() = Property(Properties.SearchQuery).save(bindQueryProperty.value)
}