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
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.viewmodel.DarkModeViewModel
import online.hudacek.fxradio.viewmodel.InfoPanelState
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.StationInfoViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.action
import tornadofx.addClass
import tornadofx.bind
import tornadofx.booleanBinding
import tornadofx.c
import tornadofx.contextmenu
import tornadofx.controlsfx.glyph
import tornadofx.disableWhen
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.item
import tornadofx.objectBinding
import tornadofx.onChange
import tornadofx.paddingLeft
import tornadofx.paddingRight
import tornadofx.paddingTop
import tornadofx.region
import tornadofx.slider
import tornadofx.vbox
import tornadofx.vgrow

private const val CONTROLS_GLYPH_SIZE = 22.0
private const val VOLUME_GLYPH_SIZE = 14.0
private const val INFO_GLYPH_SIZE = 14.0

/**
 * Main player view above stations
 * Play/pause, volume controls
 */
class PlayerView : BaseView() {

    private val viewModel: PlayerViewModel by inject()
    private val darkModeViewModel: DarkModeViewModel by inject()
    private val stationInfoViewModel: StationInfoViewModel by inject()

    private val playerStationView: PlayerStationView by inject()

    private val playGlyph by lazy { FontAwesome.Glyph.PLAY.make(CONTROLS_GLYPH_SIZE, useStyle = false) }
    private val stopGlyph by lazy { FontAwesome.Glyph.STOP.make(CONTROLS_GLYPH_SIZE, useStyle = false) }
    private val infoGlyph by lazy {
        FontAwesome.Glyph.INFO_CIRCLE.make(INFO_GLYPH_SIZE, useStyle = false,
                color = c(darkModeViewModel.appearanceProperty.value!!.primary))
    }
    private val volumeDownGlyph by lazy { FontAwesome.Glyph.VOLUME_DOWN.make(VOLUME_GLYPH_SIZE, useStyle = false) }
    private val volumeUpGlyph by lazy { FontAwesome.Glyph.VOLUME_UP.make(VOLUME_GLYPH_SIZE, useStyle = false) }

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

    private val stationInfo by lazy {
        glyph {
            id = "stationInfo"
            graphic = infoGlyph
            disableWhen {
                viewModel.stationProperty.booleanBinding {
                    it == null || !it.isValid()
                }
            }
            setOnMouseClicked {
                stationInfoViewModel.stateProperty.apply {
                    value = if (value == InfoPanelState.Shown) {
                        InfoPanelState.Hidden
                    } else {
                        InfoPanelState.Shown
                    }
                }
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

            // Show station details
            add(stationInfo)

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
