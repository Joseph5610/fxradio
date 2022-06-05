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

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.setOnSpacePressed
import online.hudacek.fxradio.ui.style.Appearance
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.viewmodel.HistoryViewModel
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.glyph

private const val controlsGlyphSize = 22.0
private const val volumeGlyphSize = 14.0

/**
 * Main player view above stations
 * Play/pause, volume controls
 */
class PlayerView : BaseView() {

    private val viewModel: PlayerViewModel by inject()
    private val historyViewModel: HistoryViewModel by inject()

    private val playerStationView: PlayerStationView by inject()

    private val playGlyph by lazy { FontAwesome.Glyph.PLAY.make(controlsGlyphSize, useStyle = false) }
    private val stopGlyph by lazy { FontAwesome.Glyph.STOP.make(controlsGlyphSize, useStyle = false) }
    private val volumeDownGlyph by lazy { FontAwesome.Glyph.VOLUME_DOWN.make(volumeGlyphSize, useStyle = false) }
    private val randomStationGlyph by lazy {
        FontAwesome.Glyph.RANDOM.make(volumeGlyphSize, useStyle = false, color = c(Appearance.currentAppearance.primary))
    }
    private val volumeUpGlyph by lazy { FontAwesome.Glyph.VOLUME_UP.make(volumeGlyphSize, useStyle = false) }

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
            //isShowTickMarks = true
            paddingTop = 2.0

            //Save new value
            valueProperty().onChange {
                viewModel.commit()
            }
        }
    }

    override val root = vbox {
        hbox(spacing = 12) {
            vgrow = Priority.NEVER
            alignment = Pos.CENTER_LEFT
            paddingLeft = 30.0

            if (Config.Flags.enableDebugWindow) {
                contextmenu {
                    item("Debug Window").action {
                        Modal.Debug.open()
                    }
                }
            }

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
                alignment = Pos.CENTER
                glyph {
                    id = "volumeMinIcon"
                    graphic = volumeDownGlyph
                    setOnMouseClicked {
                        volumeSlider.value = volumeSlider.min
                    }
                    addClass(Styles.playerControls)
                }
                add(volumeSlider)
                glyph {
                    id = "volumeMaxIcon"
                    graphic = volumeUpGlyph
                    minWidth = 20.0
                    setOnMouseClicked {
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