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

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.events.PlaybackMetaChangedEvent
import online.hudacek.fxradio.extension.createImage
import online.hudacek.fxradio.extension.notification
import online.hudacek.fxradio.extension.tickerView
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

/**
 * Player info box - now playing song, radio logo, radio name
 */
class PlayerStationBoxView : View() {

    private val playerViewModel: PlayerViewModel by inject()

    private var stationLogo = imageview(Config.Resources.musicIcon) {
        effect = DropShadow(20.0, Color.WHITE)
        fitWidth = 30.0
        isPreserveRatio = true
    }

    private val stationNameAnimated = tickerView()
    private val stationNameStatic = label()

    private var stationNameBox = vbox(alignment = Pos.CENTER) {
        if (playerViewModel.animateProperty.value) {
            add(stationNameAnimated)
        } else {
            add(stationNameStatic)
        }
    }

    val nowStreamingLabel = label(messages["player.streamingStopped"]) {
        id = "nowStreaming"
        addClass(Styles.grayLabel)
    }

    init {
        //Subscribe to property changes
        playerViewModel.stationProperty.onChange { it?.let(::onStationChange) }
        playerViewModel.animateProperty.onChange(::onAnimatePropertyChanged)
        subscribe<PlaybackMetaChangedEvent> { it.let(::onMetaDataUpdated) }
    }

    override val root = hbox(5) {
        addClass(Styles.playerStationBox)

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
                add(stationNameBox)
            }
            bottom {
                vbox(alignment = Pos.CENTER) {
                    add(nowStreamingLabel)
                }
            }
        }
    }

    private fun onStationChange(station: Station) {
        with(station) {
            if (isValidStation()) {
                updateStationName(name)
                stationLogo.createImage(this)
            }
        }
    }

    /**
     * Show/Hide ticker with radio name / Now playing details
     * Called when user changes the settings in Player menu
     */
    private fun onAnimatePropertyChanged(shouldAnimate: Boolean) {
        if (shouldAnimate) {
            stationNameBox.replaceChildren(stationNameAnimated)
        } else {
            stationNameBox.replaceChildren(stationNameStatic)
        }
        onStationChange(playerViewModel.stationProperty.value)
    }

    /**
     * Called when new song starts playing or other metadata of stream changes
     * @param event new stream Meta Data
     */
    private fun onMetaDataUpdated(event: PlaybackMetaChangedEvent) {
        val newSongName = event.mediaMeta.nowPlaying.trim()
        val newStreamTitle = event.mediaMeta.title.trim()

        //Do not update if song name is too short
        if (newSongName.length > 1) {

            val actualTitle = if (newStreamTitle.isNotEmpty()) {
                newStreamTitle
            } else {
                playerViewModel.stationProperty.value.name
            }

            if (playerViewModel.notificationsProperty.value) {
                notification(
                        title = newSongName,
                        subtitle = actualTitle)
            }

            updateStationName(newSongName)
            nowStreamingLabel.text = actualTitle
        }
    }

    //change station name for static or animated view
    private fun updateStationName(stationName: String) {
        if (playerViewModel.animateProperty.value) stationNameAnimated.updateText(stationName)
        else {
            stationNameStatic.text = stationName
            stationNameStatic.tooltip = Tooltip(stationName)
        }
    }
}
