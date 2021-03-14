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

import io.reactivex.Single
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.api.model.VoteResponse
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.events.AppNotification
import online.hudacek.fxradio.utils.applySchedulers
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.ItemViewModel
import tornadofx.get
import tornadofx.observableListOf
import tornadofx.property

class StationInfo(station: Station = Station.dummy) {
    var station: Station by property(station)
    var name: String by property(station.name)
    var country: String by property(station.country)
    var language: String by property(station.language)
    var codec: String by property(station.codec)
    var bitrate: Int by property(station.bitrate)
    var votes: Int by property(station.votes)
    var tags: ObservableList<String> by property(observableListOf(
            station.tags
                    .split(",")
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }
    ))
    var homePage: String by property(station.homepage)
}

class StationInfoViewModel : ItemViewModel<StationInfo>() {
    private val appEvent: AppEvent by inject()

    val stationProperty = bind(StationInfo::station) as ObjectProperty
    val tagsProperty = bind(StationInfo::tags) as ListProperty<String>
    val homePageProperty = bind(StationInfo::homePage) as StringProperty
    val nameProperty = bind(StationInfo::name) as StringProperty
    val codecProperty = bind(StationInfo::codec) as StringProperty
    val bitrateProperty = bind(StationInfo::bitrate) as IntegerProperty
    val languageProperty = bind(StationInfo::language) as StringProperty
    val countryProperty = bind(StationInfo::country) as StringProperty
    val votesProperty = bind(StationInfo::votes) as IntegerProperty

    init {
        //Increase vote count on the server
        appEvent.vote
                .flatMapSingle {
                    StationsApi.service
                            .vote(it.stationuuid)
                            .compose(applySchedulers())
                            .onErrorResumeNext { Single.just(VoteResponse(false, "Voting returned error response")) }
                }
                .subscribe {
                    if (!it.ok) {
                        //Why this API returns error 200 on error ...
                        appEvent.appNotification.onNext(
                                AppNotification(messages["vote.error"],
                                        FontAwesome.Glyph.WARNING))
                    } else {
                        appEvent.appNotification.onNext(
                                AppNotification(messages["vote.ok"],
                                        FontAwesome.Glyph.CHECK))
                    }
                }
    }
}