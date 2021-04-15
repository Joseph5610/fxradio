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

package online.hudacek.fxradio.ui.modal

import javafx.geometry.Pos
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.openUrl
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.ServersViewModel
import online.hudacek.fxradio.viewmodel.StatsState
import online.hudacek.fxradio.viewmodel.StatsViewModel
import tornadofx.*

/**
 * Modal window that shows status of API server
 *
 */
class StatsFragment : BaseFragment() {

    private val serversViewModel: ServersViewModel by inject()
    private val viewModel: StatsViewModel by inject()

    override fun onDock() = viewModel.fetchStats()

    private val labelTextProperty = viewModel.stateProperty.stringBinding {
        when (it) {
            is StatsState.Loading -> {
                messages["loading"]
            }
            is StatsState.Error -> {
                messages["stats.statsUnavailable"]
            }
            else -> {
                ""
            }
        }
    }

    override val root = vbox {
        title = messages["stats.title"]
        setPrefSize(300.0, 250.0)

        vbox(alignment = Pos.CENTER) {
            requestFocusOnSceneAvailable() //To get rid of the blue box around the hyperlink
            hyperlink(serversViewModel.selectedProperty) {
                paddingAll = 10.0
                addClass(Styles.header)
                action {
                    app.openUrl("http://${this.text}")
                }
            }

            vbox {
                alignment = Pos.BASELINE_CENTER
                label(labelTextProperty) {
                    paddingAll = 20.0
                }
                showWhen {
                    viewModel.stateProperty.isEqualTo(StatsState.Loading)
                }
            }
        }

        listview(viewModel.statsProperty) {
            isMouseTransparent = true //Disable clicking into listview, as it is not needed for this listview
            cellFormat {
                paddingAll = 0.0
                graphic = hbox(5) {
                    label(messages[item.first] + ":")
                    label(item.second)
                    addClass(Styles.libraryListItem)
                }
            }

            showWhen {
                viewModel.stateProperty.booleanBinding {
                    when (it) {
                        is StatsState.Fetched -> true
                        else -> false
                    }
                }
            }
            addClass(Styles.libraryListView)
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}