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
import online.hudacek.fxradio.events.PlaybackChangeEvent
import online.hudacek.fxradio.events.PlaybackMetaChangedEvent
import online.hudacek.fxradio.events.PlayingStatus
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.*
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

/**
 * Player info box - now playing song, radio logo, radio name
 */
class PlayerStationBoxView : View() {

    private val playerViewModel: PlayerViewModel by inject()

    private val ticker = tickerView()
    private val stationNameProperty = stringProperty()

    //Right click menu on
    private val copyMenu = copyMenu(clipboard, name = messages["copy"]) {
        items[0].apply {
            isDisable = true
        }
        item(messages["copy.stream.url"]) {
            isDisable = true
        }
        item(messages["search.on.youtube"]) {
            isDisable = true
        }
    }

    private val stationLogo = imageview(Config.Resources.musicIcon) {
        effect = DropShadow(20.0, Color.WHITE)
        fitWidth = 30.0
        isPreserveRatio = true
    }

    private val stationNameBox = vbox(alignment = Pos.CENTER) {
        vbox {
            add(ticker)
            showWhen {
                playerViewModel.animateProperty
            }
        }

        label(stationNameProperty) {
            showWhen {
                playerViewModel.animateProperty.not()
            }
        }
    }

    private val nowStreamingLabel = label(messages["player.streamingStopped"]) {
        id = "nowStreaming"
        addClass(Styles.grayLabel)
    }

    init {
        //Subscribe to property changes
        ticker.tickerTextProperty.bindBidirectional(stationNameProperty)
        playerViewModel.stationProperty.onChange { it?.let(::onStationChange) }
        subscribe<PlaybackMetaChangedEvent> { it.let(::onMetaDataUpdated) }
        subscribe<PlaybackChangeEvent> { it.playingStatus.let(::onPlaybackStatusChanged) }
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
            if (isValid()) {
                updateStationName(name)
                copyMenu.apply {
                    items[1].apply {
                        isDisable = false
                        action {
                            url_resolved?.let { url -> clipboard.update(url) }
                        }
                    }
                }
                stationLogo.createImage(this)
            }
        }
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
        this.stationNameProperty.value = stationName
        copyMenu.apply {
            items[0].apply {
                isDisable = false
                action {
                    clipboard.update(stationName)
                }
            }
            items[2].apply {
                isDisable = false
                action {
                    app.openUrl(Config.Paths.youtubeSearchUrl, stationName)
                }
            }
        }
    }

    fun onPlaybackStatusChanged(playingStatus: PlayingStatus) {
        if (playingStatus == PlayingStatus.Stopped) {
            nowStreamingLabel.text = messages["player.streamingStopped"]
        } else {
            nowStreamingLabel.text = messages["player.nowStreaming"]
        }
    }
}