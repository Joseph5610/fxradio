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

package online.hudacek.fxradio.fragments

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.schedulers.Schedulers
import javafx.geometry.Pos
import online.hudacek.fxradio.StationsApi
import online.hudacek.fxradio.extension.copyMenu
import online.hudacek.fxradio.extension.openUrl
import online.hudacek.fxradio.extension.requestFocusOnSceneAvailable
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.views.ProgressView
import tornadofx.*

/**
 * Modal window that shows status of API server
 */
class StatsFragment : Fragment() {

    data class StatsList(val key: String, val value: String)

    private var container = vbox {
        add(ProgressView::class)
    }

    private val stationsApi: StationsApi
        get() = StationsApi.client

    override fun onBeforeShow() {
        currentWindow?.opacity = 0.85
    }

    init {
        title = messages["title"]
        stationsApi.getStats()
                .subscribeOn(Schedulers.io())
                .observeOnFx()
                .subscribe({

                    val mappedValuesList = observableListOf(
                            StatsList(messages["stats.status"], it.status),
                            StatsList(messages["stats.apiVersion"], it.software_version),
                            StatsList(messages["stats.supportedVersion"], it.supported_version.toString()),
                            StatsList(messages["stats.stations"], it.stations.toString()),
                            StatsList(messages["stats.countries"], it.countries.toString()),
                            StatsList(messages["stats.brokenStations"], it.stations_broken.toString()),
                            StatsList(messages["stats.tags"], it.tags.toString())
                    )

                    container.replaceChildren(listview(mappedValuesList) {
                        cellFormat {
                            paddingAll = 0.0
                            graphic = hbox(5) {
                                label(item.key + ":")
                                label(item.value)
                                addClass(Styles.libraryListItem)
                            }
                            copyMenu(clipboard,
                                    name = messages["copy"],
                                    value = "${item.key}: ${item.value}")
                        }
                        addClass(Styles.libraryListView)
                    })
                }, {
                    container.replaceChildren(
                            vbox {
                                alignment = Pos.BASELINE_CENTER
                                label(messages["statsUnavailable"]) {
                                    paddingAll = 20.0
                                }
                            }
                    )
                })
    }

    override val root = vbox {
        setPrefSize(300.0, 250.0)

        vbox(alignment = Pos.CENTER) {
            requestFocusOnSceneAvailable()
            hyperlink(StationsApi.hostname) {
                paddingAll = 10.0
                addClass(Styles.header)
                action {
                    app.openUrl("http://${this.text}")
                }
            }
            add(container)
        }
    }
}