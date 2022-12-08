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
import online.hudacek.fxradio.ui.util.openUrl
import online.hudacek.fxradio.ui.util.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.NoSelectionModel
import online.hudacek.fxradio.viewmodel.ServersViewModel
import online.hudacek.fxradio.viewmodel.StatsState
import online.hudacek.fxradio.viewmodel.StatsViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.booleanBinding
import tornadofx.get
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
        setPrefSize(300.0, 260.0)
        paddingAll = 5.0

        vbox(alignment = Pos.CENTER) {
            requestFocusOnSceneAvailable()
            paddingAll = 5.0

            hyperlink(serversViewModel.selectedProperty) {
                action {
                    app.openUrl("https://$text")
                }
                addClass(Styles.header)
                addClass(Styles.primaryTextColor)
            }

            label(labelTextProperty) {
                paddingAll = 20.0

                showWhen {
                    viewModel.stateProperty.booleanBinding {
                        it !is StatsState.Fetched
                    }
                }
            }
        }

        listview(viewModel.statsListProperty) {
            selectionModel = NoSelectionModel()

            cellFormat {
                addClass(Styles.decoratedListItem)
            }

            cellCache { label(it) }

            showWhen {
                viewModel.stateProperty.booleanBinding {
                    when (it) {
                        is StatsState.Fetched -> true
                        else -> false
                    }
                }
            }
            addClass(Styles.decoratedListView)
        }
        addClass(Styles.backgroundWhite)
    }
}
