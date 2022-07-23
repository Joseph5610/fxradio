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

package online.hudacek.fxradio.ui.fragment

import javafx.geometry.Pos
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.openUrl
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.ServersViewModel
import online.hudacek.fxradio.viewmodel.StatsState
import online.hudacek.fxradio.viewmodel.StatsViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.booleanBinding
import tornadofx.get
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.listview
import tornadofx.paddingAll
import tornadofx.stringBinding
import tornadofx.vbox

/**
 * Fragment that presents stats of currently used API server
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
        setPrefSize(300.0, 230.0)

        vbox(alignment = Pos.CENTER) {
            paddingAll = 10.0

            hyperlink(serversViewModel.selectedProperty) {
                action {
                    app.openUrl("http://${this.text}")
                }
                addClass(Styles.header)
                addClass(Styles.primaryTextColor)
            }

            vbox {
                alignment = Pos.BASELINE_CENTER
                label(labelTextProperty) {
                    paddingAll = 20.0
                }
                showWhen {
                    viewModel.stateProperty.booleanBinding {
                        it !is StatsState.Fetched
                    }
                }
            }
        }

        listview(viewModel.statsProperty) {
            requestFocusOnSceneAvailable() // To get rid of the blue box around the hyperlink
            isMouseTransparent = true // Disable clicking into listview, as it is not needed for this listview
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