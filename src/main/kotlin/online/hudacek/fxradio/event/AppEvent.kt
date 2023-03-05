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

import io.reactivex.rxjava3.subjects.BehaviorSubject
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.media.StreamMetaData
import tornadofx.Controller

/**
 * General App Events
 */
class AppEvent : Controller() {

    val appNotification = BehaviorSubject.create<AppNotification>()
    val streamMetaDataUpdates = BehaviorSubject.create<StreamMetaData>()
    val votedStations = BehaviorSubject.create<Station>()
}
