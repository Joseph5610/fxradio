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

package online.hudacek.fxradio.views.player

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import online.hudacek.fxradio.Property
import online.hudacek.fxradio.Properties
import online.hudacek.fxradio.media.PlayerType
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.glyph
import online.hudacek.fxradio.utils.requestFocusOnSceneAvailable
import online.hudacek.fxradio.utils.setOnSpacePressed
import online.hudacek.fxradio.viewmodel.PlayerModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.PlayingStatus
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

/**
 * Main player view above stations
 * Play/pause, volume controls
 */
class PlayerMainView : View() {

    private val playerViewModel: PlayerViewModel by inject()

    private val playerStationBoxView: PlayerStationBoxView by inject()

    private val playGlyph = glyph(FontAwesome.Glyph.PLAY, size = 22.0, useStyle = false)
    private val stopGlyph = glyph(FontAwesome.Glyph.STOP, size = 22.0, useStyle = false)

    private val volumeDown = glyph(FontAwesome.Glyph.VOLUME_DOWN, size = 16.0, useStyle = false)
    private val volumeUp = glyph(FontAwesome.Glyph.VOLUME_UP, size = 16.0, useStyle = false)

    private val playerControlsBinding = playerViewModel.playingStatusProperty.objectBinding {
        if (it == PlayingStatus.Playing) {
            stopGlyph
        } else {
            playGlyph
        }
    }

    private val playerControls = button {
        id = "playerControls"
        graphicProperty().bind(playerControlsBinding)
        requestFocusOnSceneAvailable()
        disableWhen {
            playerViewModel.stationProperty.booleanBinding {
                it == null || !it.isValid()
            }
        }
        addClass(Styles.playerControls)
        action {
            playerViewModel.togglePlayer()
        }
    }

    private val volumeSlider by lazy {
        slider(-30..5) {
            bind(playerViewModel.volumeProperty)

            maxWidth = 100.0
            id = "volumeSlider"
            majorTickUnit = 8.0
            isSnapToTicks = true
            isShowTickMarks = true
            paddingTop = 10.0
        }
    }

    init {
        playerViewModel.item = PlayerModel(
                animate = Property(Properties.PLAYER_ANIMATE).get(true),
                playerType = PlayerType.valueOf(Property(Properties.PLAYER).get("VLC")),
                notifications = Property(Properties.NOTIFICATIONS).get(true),
                volume = Property(Properties.VOLUME).get(0.0))
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
            add(playerStationBoxView)

            region {
                hgrow = Priority.ALWAYS
            }

            //Volume controls
            hbox {
                paddingRight = 30.0
                alignment = Pos.CENTER_LEFT
                button {
                    id = "volumeMinIcon"
                    addClass(Styles.playerControls)
                    graphic = volumeDown
                    onLeftClick {
                        volumeSlider.value = volumeSlider.min
                    }
                }
                add(volumeSlider)
                button {
                    id = "volumeMaxIcon"
                    addClass(Styles.playerControls)
                    graphic = volumeUp
                    minWidth = 20.0
                    onLeftClick {
                        volumeSlider.value = volumeSlider.max
                    }
                }
            }
        }
        addClass(Styles.playerMainBox)
        addClass(Styles.backgroundWhiteSmoke)
    }

    override fun onDock() {
        currentWindow?.setOnSpacePressed {
            playerViewModel.togglePlayer()
        }
    }
}