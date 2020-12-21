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
import online.hudacek.fxradio.media.MetaDataChanged
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.PlayerState
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import online.hudacek.fxradio.utils.*
import tornadofx.*

/**
 * Player info box - now playing song, radio logo, radio name
 */
class PlayerStationBoxView : View() {

    private val viewModel: PlayerViewModel by inject()

    private val ticker by lazy {
        tickerView(viewModel.trackNameProperty)
    }

    private val playingStatusLabel = viewModel.playerStateProperty.stringBinding {
        when (it) {
            PlayerState.Stopped -> messages["player.streamingStopped"]
            PlayerState.Error -> messages["player.streamingError"]
            else -> viewModel.stationProperty.value.name
        }
    }

    private val stationLogo = imageview(defaultRadioLogo) {
        effect = DropShadow(20.0, Color.WHITE)
        fitWidth = 30.0
        isPreserveRatio = true
    }

    private val nowStreamingLabel = label(playingStatusLabel) {
        id = "nowStreaming"
        addClass(Styles.grayLabel)
    }

    init {
        //Subscribe to property changes
        viewModel.stationProperty.stationImage(stationLogo)
        subscribe<MetaDataChanged> { it.let(::onMetaDataChanged) }
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
                        add(ticker)
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
                    add(nowStreamingLabel)
                }
            }
        }
        addClass(Styles.playerStationBox)
    }

    /**
     * Called when new song starts playing or other metadata of stream changes
     * @param event new stream Meta Data
     */
    private fun onMetaDataChanged(event: MetaDataChanged) {
        val newSongName = event.newMetaData.nowPlaying.trim()
        val newStreamTitle = event.newMetaData.stationName.trim()

        //Do not update if song name is too short
        if (newSongName.length > 1) {
            ticker.isScheduled.value = true
            if (newStreamTitle.isNotEmpty()) {
                if (viewModel.notificationsProperty.value) {
                    notification(title = newSongName, subtitle = newStreamTitle)
                }

                if (newStreamTitle != viewModel.stationProperty.value.name) {
                    viewModel.trackNameProperty.value = "$newStreamTitle - $newSongName"
                } else {
                    viewModel.trackNameProperty.value = newSongName
                }
            } else {
                viewModel.trackNameProperty.value = newSongName
            }
        }
    }
}