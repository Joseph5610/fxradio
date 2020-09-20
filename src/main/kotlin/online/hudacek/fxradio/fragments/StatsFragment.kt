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

import javafx.geometry.Pos
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.extension.copyMenu
import online.hudacek.fxradio.extension.openUrl
import online.hudacek.fxradio.extension.requestFocusOnSceneAvailable
import online.hudacek.fxradio.extension.showWhen
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.viewmodel.StatsViewModel
import tornadofx.*

/**
 * Modal window that shows status of API server
 */
class StatsFragment : Fragment() {

    private val statsViewModel: StatsViewModel by inject()

    override fun onBeforeShow() {
        currentWindow?.opacity = 0.85
    }

    init {
        title = messages["stats.title"]
        statsViewModel.getStats()
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

            vbox {
                alignment = Pos.BASELINE_CENTER
                label(messages["statsUnavailable"]) {
                    paddingAll = 20.0
                }
                showWhen {
                    statsViewModel.isError
                }
            }

            progressindicator {
                minHeight = 50.0
                showWhen {
                    statsViewModel.isError.not().and(booleanBinding(statsViewModel.statsProperty) {
                        isEmpty()
                    })
                }
            }
        }

        listview(statsViewModel.statsProperty) {
            cellFormat {
                paddingAll = 0.0
                graphic = hbox(5) {
                    label(item.first + ":")
                    label(item.second)
                    addClass(Styles.libraryListItem)
                }
                copyMenu(clipboard,
                        name = messages["copy"],
                        value = "${item.first}: ${item.second}")
            }
            addClass(Styles.libraryListView)
        }
    }
}