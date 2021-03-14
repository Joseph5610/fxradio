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

package online.hudacek.fxradio.events

import io.reactivex.subjects.BehaviorSubject
import online.hudacek.fxradio.api.model.Country
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.ui.viewmodel.LibraryType
import tornadofx.Controller

/**
 * General App Events
 */
class AppEvent : Controller() {
    val appNotification = BehaviorSubject.create<AppNotification>()
    val osNotification = BehaviorSubject.create<OsNotification>()

    val addFavourite = BehaviorSubject.create<Station>()
    val removeFavourite = BehaviorSubject.create<Station>()
    val cleanupFavourites = BehaviorSubject.create<Unit>()

    val cleanupHistory = BehaviorSubject.create<Unit>()

    val pinCountry = BehaviorSubject.create<Country>()
    val unpinCountry = BehaviorSubject.create<Country>()
    val refreshCountries = BehaviorSubject.create<Unit>()

    val vote = BehaviorSubject.create<Station>()

    val refreshLibrary = BehaviorSubject.create<LibraryType>()

    val playerMetaData = BehaviorSubject.create<MetaData>()
}