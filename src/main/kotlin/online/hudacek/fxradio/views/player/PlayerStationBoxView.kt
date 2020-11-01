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

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.media.MetaDataChanged
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.*
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.PlayingStatus
import tornadofx.*

/**
 * Player info box - now playing song, radio logo, radio name
 */
class PlayerStationBoxView : View() {

    private val playerViewModel: PlayerViewModel by inject()

    private val trackNameProperty by lazy { stringProperty() }
    //private val showEllipsisMenuProperty by lazy { booleanProperty() }

    private val ticker by lazy {
        tickerView {
            tickerTextProperty.bind(trackNameProperty)
        }
    }

    private val playingStatusLabel = playerViewModel.playingStatusProperty.stringBinding {
        when (it) {
            PlayingStatus.Stopped -> messages["player.streamingStopped"]
            PlayingStatus.Error -> messages["player.streamingError"]
            else -> playerViewModel.stationProperty.value.name
        }
    }

    private val stationLogo = imageview(Config.Resources.waveIcon) {
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
        playerViewModel.stationProperty.onChange { it?.let(::handleStationChange) }
        subscribe<MetaDataChanged> { it.let(::onMetaDataChanged) }
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

            onHover {
                //showEllipsisMenuProperty.value = it
            }

            top {
                autoUpdatingCopyMenu(clipboard, messages["copy.nowPlaying"], trackNameProperty)
                // hbox {
                vbox(alignment = Pos.CENTER) {
                    //Dynamic ticker for station name
                    vbox {
                        add(ticker)
                        showWhen {
                            playerViewModel.animateProperty
                        }
                    }

                    //Static label for station name
                    label(trackNameProperty) {
                        onHover { tooltip(text) }
                        showWhen {
                            playerViewModel.animateProperty.not()
                        }
                    }
                }
                /*
                vbox {
                    glyph(FontAwesome.Glyph.ELLIPSIS_H, useStyle = false, size = 14.0) {
                        style {
                            alignment = Pos.BASELINE_CENTER
                            paddingLeft = 10.0
                        }

                        showWhen {
                            playerViewModel.stationProperty.booleanBinding {
                                it != null && it.isValid()
                            }.and(showEllipsisMenuProperty)
                        }
                    }
                }*/
                //   }

            }
            bottom {
                vbox(alignment = Pos.CENTER) {
                    add(nowStreamingLabel)
                }
            }
        }
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
                if (playerViewModel.notificationsProperty.value) {
                    notification(title = newSongName, subtitle = newStreamTitle)
                }

                if (newStreamTitle != playerViewModel.stationProperty.value.name) {
                    trackNameProperty.value = "$newStreamTitle - $newSongName"
                } else {
                    trackNameProperty.value = newSongName
                }
            } else {
                trackNameProperty.value = newSongName
            }
        }
    }

    private fun handleStationChange(station: Station) {
        if (station.isValid()) {
            trackNameProperty.value = playerViewModel.stationProperty.value.name + " - " + messages["player.noMetaData"]
            stationLogo.createImage(station)
        } else trackNameProperty.value = ""
    }
}