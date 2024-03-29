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

package online.hudacek.fxradio.usecase

import io.reactivex.rxjava3.core.Observable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Tag
import online.hudacek.fxradio.util.applySchedulers

private const val MAX_TAGS_LIMIT = 15
private const val MIN_TAG_LENGTH = 3
private const val TAGS_ORDER = "stationcount"

/**
 * Gets most popular tags
 */
class GetTagsUseCase : BaseUseCase<Unit, Observable<Tag>>() {

    override fun execute(input: Unit): Observable<Tag> = radioBrowserApi
        .getTags(order = TAGS_ORDER, limit = MAX_TAGS_LIMIT, reverse = true)
        .flattenAsObservable { it }
        .filter { it.name.length >= MIN_TAG_LENGTH }
        .compose(applySchedulers())
}
