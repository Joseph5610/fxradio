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

package online.hudacek.fxradio.ui.viewmodel

import com.github.thomasnield.rxkotlinfx.toObservableChanges
import io.reactivex.Observable
import io.reactivex.Single
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.SearchBody
import online.hudacek.fxradio.api.model.SearchByTagBody
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.ItemViewModel
import tornadofx.property
import tornadofx.stringBinding

class Search(query: String = "",
             useTagSearch: Boolean = false) {
    var query: String by property(query)
    var searchByTag: Boolean by property(useTagSearch)
}

class SearchViewModel : ItemViewModel<Search>(Search()) {

    val searchByTagProperty = bind(Search::searchByTag) as BooleanProperty

    //Internal only, contains unedited search query
    val bindQueryProperty = bind(Search::query) as StringProperty

    //Search query is limited to 50 chars and trimmed to reduce requests to API
    val queryProperty = bindQueryProperty.stringBinding {
        if (it != null) {
            if (it.length > 50) it.substring(0, 50).trim() else it.trim()
        } else ""
    }

    val queryChanges: Observable<String> = queryProperty
            .toObservableChanges()
            .map { it.newVal }

    val searchByTagSingle: Single<List<Station>>
        get() = StationsApi.service
                .searchStationByTag(SearchByTagBody(queryProperty.value))
                .compose(applySchedulers())

    val searchByNameSingle: Single<List<Station>>
        get() = StationsApi.service
                .searchStationByName(SearchBody(queryProperty.value))
                .compose(applySchedulers())

    override fun onCommit() = Property(Properties.SEARCH_QUERY).save(bindQueryProperty.value)
}