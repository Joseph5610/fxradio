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

package online.hudacek.broadcastsfx.fragments

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.schedulers.Schedulers
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.extension.createImage
import online.hudacek.broadcastsfx.extension.openUrl
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.ProgressView
import tornadofx.*
import tornadofx.controlsfx.statusbar

class StationInfoFragment(val station: Station? = null) : Fragment() {

    private val playerModel: PlayerModel by inject()

    private val shownStation: Station = station ?: playerModel.station.value

    private val stationsApi: StationsApi
        get() = StationsApi.client

    private var container = vbox {
        add(ProgressView::class)
    }

    override fun onBeforeShow() {
        currentStage?.opacity = 0.85
    }

    override val root = vbox {
        prefWidth = 300.0
        title = shownStation.name
        add(container)
    }

    init {
        stationsApi.getStationInfo(shownStation.stationuuid)
                .observeOnFx()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    //Update station data from real time
                    showStationData(it.last())
                }, {
                    showStationData(shownStation)
                })
    }

    private fun showStationData(station: Station) {
        station.let {

            val tagsList = it.tags.split(",")
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }

            val codecBitrateInfo = it.codec + " (" + it.bitrate + " kbps)"
            container.replaceChildren(
                    vbox {
                        vbox(alignment = Pos.CENTER) {
                            paddingAll = 10.0
                            imageview {
                                createImage(it)
                                effect = DropShadow(30.0, Color.LIGHTGRAY)
                                fitHeight = 100.0
                                fitHeight = 100.0
                                isPreserveRatio = true
                            }
                        }

                        flowpane {
                            hgap = 5.0
                            vgap = 5.0
                            alignment = Pos.CENTER
                            paddingAll = 5.0
                            observableListOf(
                                    "${it.votes} votes",
                                    codecBitrateInfo,
                                    "Country: ${it.country}",
                                    "Language: ${it.language}")
                                    .forEach { info ->
                                        label(info) {
                                            addClass(Styles.grayLabel)
                                            addClass(Styles.tag)
                                        }
                                    }
                        }

                        if (tagsList.isNotEmpty()) {
                            flowpane {
                                hgap = 5.0
                                vgap = 5.0
                                alignment = Pos.CENTER
                                paddingAll = 5.0
                                tagsList.forEach { tag ->
                                    label(tag) {
                                        addClass(Styles.tag)
                                        addClass(Styles.grayLabel)
                                    }
                                }
                            }
                        }

                        if (it.homepage.isNotEmpty()) {
                            statusbar {
                                rightItems.add(
                                        hyperlink(it.homepage) {
                                            addClass(Styles.primaryTextColor)
                                            action {
                                                app.openUrl(it.homepage)
                                            }
                                        }
                                )
                            }
                        }
                    }
            )
            currentStage?.sizeToScene()
        }
    }
}