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

package online.hudacek.fxradio.ui.view.player

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.autoUpdatingCopyMenu
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

/**
 * Player info box - now playing song, radio logo, radio name
 */
class PlayerStationView : BaseView() {

    private val viewModel: PlayerViewModel by inject()
    private val tickerView by lazy { TickerView() }

    private val playingStatusLabel = viewModel.stateProperty.stringBinding {
        when (it) {
            is PlayerState.Stopped -> messages["player.streamingStopped"]
            is PlayerState.Error -> messages["player.streamingError"]
            else -> viewModel.stationProperty.value.name
        }
    }

    private val stationLogo by lazy {
        imageview {
            //Subscribe to property changes
            viewModel.stationProperty.stationImage(this)

            effect = DropShadow(20.0, Color.WHITE)
            fitWidth = 30.0
            isPreserveRatio = true
        }
    }

    override val root = hbox(5) {
        //Radio logo
        vbox(alignment = Pos.CENTER_LEFT) {
            minHeight = 30.0
            maxHeight = 30.0
            add(stationLogo)
        }

        separator(Orientation.VERTICAL)

        //Radio name and label
        borderpane {
            prefWidthProperty().bind(this@hbox.maxWidthProperty())

            top {
                autoUpdatingCopyMenu(clipboard, messages["copy.nowPlaying"], viewModel.trackNameProperty)
                vbox(alignment = Pos.CENTER) {
                    //Dynamic ticker for station name
                    vbox {
                        add(tickerView)
                        showWhen {
                            viewModel.animateProperty
                        }
                    }

                    //Static label for station name
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