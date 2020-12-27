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

import io.reactivex.subjects.BehaviorSubject
import javafx.beans.property.ListProperty
import javafx.beans.property.MapProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.utils.applySchedulers
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class StationInfoModel(station: Station = Station.stub) {
    var codec = if (station.bitrate != 0) {
        station.codec + " (${station.bitrate} kbps)"
    } else {
        station.codec
    }

    var station: Station by property(station)
    var name: String by property(station.name)

    var infoItems: ObservableMap<String, String> by property(observableMapOf(
            "info.votes" to station.votes.toString(),
            "" to codec,
            "info.country" to station.country,
            "info.language" to station.language
    ))

    var tags: ObservableList<String> by property(observableListOf(
            station.tags
                    .split(",")
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }
    ))

    var homePage: String by property(station.homepage)
}

class StationInfoViewModel : ItemViewModel<StationInfoModel>() {
    val infoItemsProperty = bind(StationInfoModel::infoItems) as MapProperty<String, String>
    val tagsProperty = bind(StationInfoModel::tags) as ListProperty<String>
    val homePageProperty = bind(StationInfoModel::homePage) as StringProperty
    val stationProperty = bind(StationInfoModel::station) as ObjectProperty
    val stationNameProperty = bind(StationInfoModel::name) as StringProperty

    val addVote = BehaviorSubject.create<Station>()

    init {
        //Increase vote count on the server
        addVote.flatMapSingle {
            StationsApi.service
                    .vote(stationProperty.value.stationuuid)
                    .compose(applySchedulers())
        }.subscribe({
            if (!it.ok) {
                //Why this API returns error 200 on error ... crazy
                fire(NotificationEvent(messages["vote.error"]))
            } else {
                fire(NotificationEvent(messages["vote.ok"], FontAwesome.Glyph.CHECK))
            }
        }, {
            fire(NotificationEvent(messages["vote.error"]))
        })
    }
}