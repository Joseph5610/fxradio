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

package online.hudacek.fxradio.event

import io.reactivex.subjects.BehaviorSubject
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.media.StreamMetaData
import online.hudacek.fxradio.viewmodel.LibraryState
import tornadofx.Controller

/**
 * General App Events
 */
class AppEvent : Controller() {

    val appNotification = BehaviorSubject.create<AppNotification>()
    val streamMetaDataUpdated = BehaviorSubject.create<StreamMetaData>()

    val addFavourite = BehaviorSubject.create<Station>()
    val removeFavourite = BehaviorSubject.create<Station>()

    val addToHistory = BehaviorSubject.create<Station>()
    val addVote = BehaviorSubject.create<Station>()
    val refreshLibrary = BehaviorSubject.create<LibraryState>()
}
