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

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import online.hudacek.fxradio.api.stations.model.Station
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.setOnSpacePressed
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.HistoryViewModel
import online.hudacek.fxradio.viewmodel.Player
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.glyph

/**
 * Main player view above stations
 * Play/pause, volume controls
 */
class PlayerView : BaseView() {

    private val viewModel: PlayerViewModel by inject()
    private val historyViewModel: HistoryViewModel by inject()

    private val playerStationView: PlayerStationView by inject()

    private val playGlyph by lazy { FontAwesome.Glyph.PLAY.make(size = 22.0, useStyle = false) }
    private val stopGlyph by lazy { FontAwesome.Glyph.STOP.make(size = 22.0, useStyle = false) }
    private val volumeDownGlyph by lazy { FontAwesome.Glyph.VOLUME_DOWN.make(size = 14.0, useStyle = false) }
    private val randomStationGlyph by lazy { FontAwesome.Glyph.RANDOM.make(size = 14.0, useStyle = false) }
    private val volumeUpGlyph by lazy { FontAwesome.Glyph.VOLUME_UP.make(size = 14.0, useStyle = false) }

    private val playerControlsBinding = viewModel.stateProperty.objectBinding {
        if (it == PlayerState.Playing) {
            stopGlyph
        } else {
            playGlyph
        }
    }

    private val playerControls by lazy {
        glyph {
            id = "playerControls"
            graphicProperty().bind(playerControlsBinding)
            requestFocusOnSceneAvailable()
            disableWhen {
                viewModel.stationProperty.booleanBinding {
                    it == null || !it.isValid()
                }
            }
            setOnMouseClicked {
                viewModel.togglePlayerState()
            }
            addClass(Styles.playerControls)
        }
    }

    private val volumeSlider by lazy {
        slider(-30..5) {
            bind(viewModel.volumeProperty)
            id = "volumeSlider"
            maxWidth = 90.0
            majorTickUnit = 8.0
            isSnapToTicks = true
            isShowTickMarks = true
            paddingTop = 10.0

            //Save new value
            valueProperty().onChange {
                viewModel.commit()
            }
        }
    }

    init {
        viewModel.item = Player(station = historyViewModel.stationsProperty.lastOrNull() ?: Station.dummy)
    }

    override val root = vbox {
        hbox(12) {
            vgrow = Priority.NEVER
            alignment = Pos.CENTER_LEFT
            paddingLeft = 30.0

            //Play/Pause buttons
            add(playerControls)

            region {
                hgrow = Priority.ALWAYS
            }

            //Station info box
            add(playerStationView)

            glyph {
                id = "playRandomStation"
                graphic = randomStationGlyph
                tooltip(messages["player.playRandomStation"])
                onLeftClick {
                    viewModel.stationProperty.value = historyViewModel.stationsProperty.filter {
                        it != viewModel.stationProperty.value
                    }.random()
                }

                enableWhen {
                    historyViewModel.stationsProperty.emptyProperty().not()
                            .and(historyViewModel.stationsProperty.sizeProperty().greaterThan(2))
                }

                addClass(Styles.playerControls)
            }

            region {
                hgrow = Priority.ALWAYS
            }

            //Volume controls
            hbox {
                paddingRight = 30.0
                alignment = Pos.CENTER_LEFT
                glyph {
                    id = "volumeMinIcon"
                    graphic = volumeDownGlyph
                    onLeftClick {
                        volumeSlider.value = volumeSlider.min
                    }
                    addClass(Styles.playerControls)
                }
                add(volumeSlider)
                glyph {
                    id = "volumeMaxIcon"
                    graphic = volumeUpGlyph
                    minWidth = 20.0
                    onLeftClick {
                        volumeSlider.value = volumeSlider.max
                    }
                    addClass(Styles.playerControls)
                }
            }
        }
        addClass(Styles.playerMainBox)
        addClass(Styles.backgroundWhiteSmoke)
    }

    override fun onDock() {
        currentWindow?.setOnSpacePressed {
            viewModel.togglePlayerState()
        }
    }
}