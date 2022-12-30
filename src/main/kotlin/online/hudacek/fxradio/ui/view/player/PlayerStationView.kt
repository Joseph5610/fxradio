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

package online.hudacek.fxradio.ui.view.player

import javafx.geometry.Orientation
import javafx.geometry.Pos
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.util.autoUpdatingCopyMenu
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.ui.util.stationView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.*

private const val LOGO_SIZE = 30.0

/**
 * Shows now playing song, radio logo, radio name
 */
class PlayerStationView : BaseView() {

    private val viewModel: PlayerViewModel by inject()
    private val selectedStationViewModel: SelectedStationViewModel by inject()

    private val tickerView: PlayerTickerView by inject()

    private val playingStatusLabel = viewModel.stateProperty.stringBinding {
        when (it) {
            is PlayerState.Stopped -> messages["player.streamingStopped"]
            is PlayerState.Error -> messages["player.streamingError"]
            else -> selectedStationViewModel.nameProperty.value
        }
    }

    private val stationLogo by lazy {
        stationView(selectedStationViewModel.stationProperty, size = LOGO_SIZE)
    }

    override val root = hbox(spacing = 5) {
        // Radio logo
        add(stationLogo)

        separator(Orientation.VERTICAL)

        // Radio name and label
        borderpane {
            prefWidthProperty().bind(this@hbox.maxWidthProperty())

            top {
                autoUpdatingCopyMenu(clipboard, messages["copy.nowPlaying"], viewModel.trackNameProperty)
                vbox(alignment = Pos.CENTER) {

                    // Dynamic ticker for station name
                    add(tickerView)

                    tickerView.root.showWhen {
                        viewModel.animateProperty
                    }

                    // Static label for station name
                    label(viewModel.trackNameProperty) {
                        onHover { tooltip(text) }
                        showWhen {
                            viewModel.animateProperty.not()
                        }
                    }
                }
            }

            bottom {
                vbox(alignment = Pos.CENTER) {
                    label(playingStatusLabel) {
                        id = "nowStreaming"
                        addClass(Styles.grayLabel)
                    }
                }
            }
        }
        addClass(Styles.playerStationBox)
    }
}
