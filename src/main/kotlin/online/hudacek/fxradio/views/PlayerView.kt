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

package online.hudacek.fxradio.views

import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.events.PlaybackChangeEvent
import online.hudacek.fxradio.events.PlayingStatus
import online.hudacek.fxradio.extension.requestFocusOnSceneAvailable
import online.hudacek.fxradio.extension.setOnSpacePressed
import online.hudacek.fxradio.extension.shouldBeDisabled
import online.hudacek.fxradio.extension.smallIcon
import online.hudacek.fxradio.media.MediaPlayerWrapper
import online.hudacek.fxradio.media.PlayerType
import online.hudacek.fxradio.viewmodel.PlayerModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.styles.Styles
import tornadofx.*

/**
 * Main player view above stations
 * Play/pause, volume controls
 */
class PlayerView : View() {

    private val playerViewModel: PlayerViewModel by inject()
    private val mediaPlayerWrapper: MediaPlayerWrapper by inject()
    private val playerStationBoxView: PlayerStationBoxView by inject()

    private val playerControlsIcon = imageview(Config.Resources.playIcon) {
        fitWidth = 30.0
        fitHeight = 30.0
        isPreserveRatio = true
    }

    private val playerControls = button {
        requestFocusOnSceneAvailable()
        shouldBeDisabled(playerViewModel.stationProperty)
        add(playerControlsIcon)
        addClass(Styles.playerControls)
        action {
            mediaPlayerWrapper.togglePlaying()
        }
    }

    init {
        val animate = app.config.boolean(Config.Keys.playerAnimate, true)
        val notifications = app.config.boolean(Config.Keys.notifications, true)
        val playerType = PlayerType.valueOf(app.config.string(Config.Keys.playerType, "VLC"))
        val volume = app.config.double(Config.Keys.volume, 0.0)

        playerViewModel.item = PlayerModel(
                animate = animate,
                playerType = playerType,
                notifications = notifications,
                volume = volume)

        //subscribe to events
        subscribe<PlaybackChangeEvent> { it.playingStatus.let(::onPlaybackStatusChanged) }
    }

    private val volumeSlider = slider(-30..5) {
        bind(playerViewModel.volumeProperty)
        majorTickUnit = 8.0
        isSnapToTicks = true
        // isShowTickMarks = true
        valueProperty().onChange {
            playerViewModel.commit()
        }
    }

    override val root = vbox {
        addClass(Styles.playerMainBox)
        hbox(15) {
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
                smallIcon(Config.Resources.volumeDownIcon) {
                    setOnMouseClicked {
                        volumeSlider.value = volumeSlider.min
                    }
                }
                add(volumeSlider)
                smallIcon(Config.Resources.volumeUpIcon) {
                    setOnMouseClicked {
                        volumeSlider.value = volumeSlider.max
                    }
                }
            }
        }
    }

    override fun onDock() {
        currentWindow?.setOnSpacePressed {
            mediaPlayerWrapper.togglePlaying()
        }
    }

    /**
     * Called when playback status is changed,
     * usually by pressing button in PlayerView or externally (e.g change of player type)
     * @param playingStatus new Playing status (Playing/Stopped)
     */
    private fun onPlaybackStatusChanged(playingStatus: PlayingStatus) {
        if (playingStatus == PlayingStatus.Stopped) {
            playerControlsIcon.image = Image(Config.Resources.playIcon)
            playerStationBoxView.nowStreamingLabel.text = messages["player.streamingStopped"]
        } else {
            playerControlsIcon.image = Image(Config.Resources.stopIcon)
            playerStationBoxView.nowStreamingLabel.text = messages["player.nowStreaming"]
        }
    }
}