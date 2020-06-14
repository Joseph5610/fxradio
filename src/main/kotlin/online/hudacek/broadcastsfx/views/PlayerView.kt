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

package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.events.MediaMetaChanged
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.extension.ui.*
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import online.hudacek.broadcastsfx.model.Player
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*

class PlayerView : View() {

    private val player: PlayerModel by inject()
    private val mediaPlayer: MediaPlayerWrapper by inject()

    private val radioNameTicker = TickerView()
    private val radioNameStaticText = label()

    private var radioNameContainer: VBox by singleAssign()
    private var radioLogo: ImageView by singleAssign()

    private val nowStreamingLabel = label(messages["streamingStopped"]) {
        addClass(Styles.grayLabel)
    }

    private val playImage = imageview(playIcon) {
        fitWidth = 30.0
        fitHeight = 30.0
        isPreserveRatio = true
    }

    private val playerControls = button {
        requestFocusOnSceneAvailable()
        shouldBeDisabled(player.station)
        add(playImage)
        addClass(Styles.playerControls)
        action {
            mediaPlayer.togglePlaying()
        }
    }

    init {
        val animate = app.config.boolean(Config.Keys.playerAnimate, true)
        val notifications = app.config.boolean(Config.Keys.notifications, true)
        val playerType = PlayerType.valueOf(app.config.string(Config.Keys.playerType, "VLC"))
        val volume = app.config.double(Config.Keys.volume, 0.0)

        player.item = Player(
                animate = animate,
                playerType = playerType,
                notifications = notifications,
                volume = volume)

        //subscribe to events
        subscribe<PlaybackChangeEvent> { it.playingStatus.let(::onPlaybackStatusChanged) }
        subscribe<MediaMetaChanged> { it.let(::onMetaDataUpdated) }

        //Subscribe to property changes
        player.station.onChange { it?.let(::onStationChange) }
        player.animate.onChange(::onAnimatePropertyChanged)
    }

    private val volumeSlider = slider(-30..5) {
        bind(player.volumeProperty)
        majorTickUnit = 8.0
        isSnapToTicks = true
        // isShowTickMarks = true
        valueProperty().onChange {
            player.commit()
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

            //Player box
            hbox(5) {
                addClass(Styles.playerStationBox)

                //Radio logo
                vbox(alignment = Pos.CENTER_LEFT) {
                    radioLogo = imageview(Config.Paths.defaultRadioIcon) {
                        effect = DropShadow(20.0, Color.WHITE)
                        fitWidth = 30.0
                        minHeight = 30.0
                        maxHeight = 30.0
                        isPreserveRatio = true
                    }
                }

                separator(Orientation.VERTICAL)

                //Radio name and label
                vbox(alignment = Pos.CENTER) {
                    fitToParentWidth()
                    radioNameContainer = vbox(alignment = Pos.CENTER) {
                        if (player.animate.value) {
                            add(radioNameTicker)
                        } else {
                            add(radioNameStaticText)
                        }
                    }
                    add(nowStreamingLabel)
                }
            }

            region {
                hgrow = Priority.ALWAYS
            }

            //Volume controls
            hbox {
                paddingRight = 30.0
                alignment = Pos.CENTER_LEFT
                smallIcon(volumeDownIcon) {
                    setOnMouseClicked {
                        volumeSlider.value = volumeSlider.min
                    }
                }
                add(volumeSlider)
                smallIcon(volumeUpIcon) {
                    setOnMouseClicked {
                        volumeSlider.value = volumeSlider.max
                    }
                }
            }
        }
    }

    /**
     * Called when playback status is changed,
     * usually by pressing button in PlayerView or externally (e.g change of player type)
     * @param playingStatus new Playing status (Playing/Stopped)
     */
    private fun onPlaybackStatusChanged(playingStatus: PlayingStatus) {
        if (playingStatus == PlayingStatus.Stopped) {
            playImage.image = Image(playIcon)
            nowStreamingLabel.text = messages["streamingStopped"]
        } else {
            playImage.image = Image(stopIcon)
            nowStreamingLabel.text = messages["nowStreaming"]
        }
    }

    /**
     * Called when new song starts playing or other metadata of stream changes
     * @param event new stream Meta Data
     */
    private fun onMetaDataUpdated(event: MediaMetaChanged) {
        val newSongName = event.mediaMeta.nowPlaying
        val newStreamTitle = event.mediaMeta.title

        if (player.notifications.value) {
            notification(
                    identifier = event.mediaMeta.title.asBase64(),
                    title = newSongName,
                    subtitle = newStreamTitle)
        }

        if (player.animate.value) radioNameTicker.updateText(newSongName)
        else {
            radioNameStaticText.text = newSongName
            radioNameStaticText.tooltip = Tooltip(newSongName)
        }

        if (newStreamTitle.isNotEmpty()) {
            nowStreamingLabel.text = newStreamTitle
        }
    }

    /**
     * Show/Hide ticker with radio name / Now playing details
     * Called when user changes the settings in Player menu
     */
    private fun onAnimatePropertyChanged(shouldAnimate: Boolean) {
        if (shouldAnimate) {
            radioNameTicker.play()
            radioNameContainer.replaceChildren(radioNameTicker)
        } else {
            radioNameTicker.stop()
            radioNameContainer.replaceChildren(radioNameStaticText)
        }
        onStationChange(player.station.value)
    }

    private fun onStationChange(station: Station) {
        with(station) {
            if (isValidStation()) {
                onPlaybackStatusChanged(mediaPlayer.playingStatus)
                if (player.animate.value) radioNameTicker.updateText(name)
                else {
                    radioNameStaticText.text = name
                    radioNameStaticText.tooltip = null
                }
                radioLogo.createImage(this)
                radioLogo.copyMenu(clipboard,
                        name = messages["copy.image.url"],
                        value = this.favicon ?: "")
            }
        }
    }

    private companion object {
        private const val playIcon = "Media-Controls-Play-icon.png"
        private const val stopIcon = "Media-Controls-Stop-icon.png"
        private const val volumeDownIcon = "Media-Controls-Volume-Down-icon.png"
        private const val volumeUpIcon = "Media-Controls-Volume-Up-icon.png"
    }
}