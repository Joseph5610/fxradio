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

import com.github.thomasnield.rxkotlinfx.toBinding
import com.github.thomasnield.rxkotlinfx.toObservable
import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import io.reactivex.Observable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.usecase.SearchUseCase
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.save
import online.hudacek.fxradio.util.value
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
            .map { if (it.length > maxQueryLength) it.substring(0, maxQueryLength).trim() else it.trim() }

    val queryBinding = bindQueryProperty.toObservable()
            .map { if (it.length > maxQueryLength) it.substring(0, maxQueryLength).trim() else it.trim() }
            .toBinding()

    fun search() = searchUseCase.execute(searchByTagProperty.value to queryBinding.value)

    override fun onCommit() = Properties.SearchQuery.save(bindQueryProperty.value)

    companion object {
        private const val maxQueryLength = 50
    }
}