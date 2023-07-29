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

import javafx.beans.property.BooleanProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.save
import online.hudacek.fxradio.util.toBinding
import online.hudacek.fxradio.util.toObservable
import online.hudacek.fxradio.util.value
import tornadofx.property

/**
 * Maximum search chars
 */
private const val QUERY_LENGTH = 50

class Search(
    query: String = Properties.SearchQuery.value(""),
    useTagSearch: Boolean = false
) {
    var query: String by property(query)
    var searchByTag: Boolean by property(useTagSearch)
}

class SearchViewModel : BaseViewModel<Search>(Search()) {

    val searchByTagProperty = bind(Search::searchByTag) as BooleanProperty

    // Internal only, contains unedited search query
    val bindQueryProperty = bind(Search::query) as StringProperty

    val queryBinding = bindQueryProperty.toObservable()
        .map { trimQuery(it) }
        .toBinding()

    /**
     * Trims the [query] so that it always contain at max [QUERY_LENGTH] chars
     */
    private fun trimQuery(query: String) =
        if (query.length > QUERY_LENGTH) query.substring(0, QUERY_LENGTH).trim() else query.trim()

    override fun onCommit() = Properties.SearchQuery.save(bindQueryProperty.value)
}
