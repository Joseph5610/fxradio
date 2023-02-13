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
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.util.setOnSpacePressed
import online.hudacek.fxradio.usecase.GetCoverArtUseCase
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.keyCombination
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.util.value
import online.hudacek.fxradio.viewmodel.InfoPanelState
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.addClass
import tornadofx.bind
import tornadofx.booleanBinding
import tornadofx.controlsfx.glyph
import tornadofx.disableWhen
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.insets
import tornadofx.objectBinding
import tornadofx.onChange
import tornadofx.onLeftClick
import tornadofx.paddingTop
import tornadofx.region
import tornadofx.slider
import tornadofx.vgrow

private const val CONTROLS_GLYPH_SIZE = 22.0
private const val VOLUME_GLYPH_SIZE = 14.0
private const val INFO_GLYPH_SIZE = 16.0

/**
 * Main player view above stations
 * Play/pause, volume controls
 */
class PlayerView : BaseView() {

    private val viewModel: PlayerViewModel by inject()
    private val selectedStationViewModel: SelectedStationViewModel by inject()

    private val playerStationView: PlayerStationView by inject()

    private val playGlyph by lazy {
        FontAwesome.Glyph.PLAY.make(CONTROLS_GLYPH_SIZE, isPrimary = false) {
            padding = insets(5, 7, 5, 7)
        }
    }

    private val pauseGlyph by lazy {
        FontAwesome.Glyph.PAUSE.make(CONTROLS_GLYPH_SIZE, isPrimary = false) {
            padding = insets(5, 7, 5, 7)
        }
    }

    private val infoGlyph by lazy {
        FontAwesome.Glyph.INFO_CIRCLE.make(INFO_GLYPH_SIZE) {
            id = "stationInfo"
            padding = insets(5, 7, 5, 7)

            disableWhen {
                selectedStationViewModel.stationProperty.booleanBinding {
                    it == null || !it.isValid()
                }
            }

            onLeftClick {
                toggleInfoPanelState()
            }

            shortcut(keyCombination(KeyCode.I)) {
                toggleInfoPanelState()
            }

            addClass(Styles.playerControlsBorder)
            addClass(Styles.playerControls)
        }
    }

    private val volumeDownGlyph by lazy {
        FontAwesome.Glyph.VOLUME_DOWN.make(VOLUME_GLYPH_SIZE, isPrimary = false) {
            id = "volumeMinIcon"
            onLeftClick {
                volumeSlider.value = volumeSlider.min
            }
            addClass(Styles.playerControls)
        }
    }

    private val volumeUpGlyph by lazy {
        FontAwesome.Glyph.VOLUME_UP.make(VOLUME_GLYPH_SIZE, isPrimary = false) {
            id = "volumeMaxIcon"
            minWidth = 20.0
            onLeftClick {
                volumeSlider.value = volumeSlider.max
            }
            addClass(Styles.playerControls)
        }
    }

    private val playerControlsBinding = viewModel.stateProperty.objectBinding {
        if (it is PlayerState.Playing) {
            pauseGlyph
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
                selectedStationViewModel.stationProperty.booleanBinding {
                    it == null || !it.isValid()
                }
            }
            onLeftClick {
                viewModel.togglePlayerState()
            }

            addClass(Styles.playerControlsBorder)
            addClass(Styles.playerControls)
        }
    }


    private val volumeSlider by lazy {
        slider(-35..1) {
            bind(viewModel.volumeProperty)
            id = "volumeSlider"
            maxWidth = 90.0
            majorTickUnit = 8.0
            isSnapToTicks = true
            paddingTop = 2.0

            valueProperty().onChange {
                viewModel.commit()
            }
        }
    }

    override val root = hbox(spacing = 6) {
        vgrow = Priority.NEVER
        alignment = Pos.CENTER_LEFT

        // Play/Pause buttons
        add(playerControls)

        region {
            hgrow = Priority.ALWAYS
        }

        // Station info box
        add(playerStationView)

        // Show station details
        add(infoGlyph)

        region {
            hgrow = Priority.ALWAYS
        }

        //Volume controls
        hbox {
            alignment = Pos.CENTER
            add(volumeDownGlyph)
            add(volumeSlider)
            add(volumeUpGlyph)
        }

        if (Properties.EnableDebugView.value(false)) {
            add(FontAwesome.Glyph.BUG.make(VOLUME_GLYPH_SIZE, isPrimary = false) {
                onLeftClick {
                    Modal.Debug.open()
                }
            })
        }

        addClass(Styles.playerMainBox)
        addClass(Styles.backgroundWhiteSmoke)
    }

    override fun onDock() {
        currentWindow?.setOnSpacePressed {
            viewModel.togglePlayerState()
        }
        viewModel.initializePlayer()
    }

    private fun toggleInfoPanelState() {
        selectedStationViewModel.stateProperty.apply {
            value = if (value == InfoPanelState.Shown) {
                InfoPanelState.Hidden
            } else {
                InfoPanelState.Shown
            }
        }
    }
}
