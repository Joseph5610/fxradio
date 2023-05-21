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

package online.hudacek.fxradio.media.player.vlc

import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.fxradio.event.AppEvent
import online.hudacek.fxradio.media.StreamMetaData
import online.hudacek.fxradio.ui.util.msgFormat
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.FX.Companion.messages
import tornadofx.find
import tornadofx.get
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.Meta
import uk.co.caprica.vlcj.player.base.State

private val logger = KotlinLogging.logger {}

/**
 * Adapter that handles changes of metaData in VLC and sends it to App playerMetaData event
 */
class VLCMediaAdapter : MediaEventAdapter() {

    private val appEvent = find<AppEvent>()

    override fun mediaStateChanged(media: Media?, newState: State) {
        media?.let {
            if (newState == State.ERROR || newState == State.ENDED) {
                Platform.runLater {
                    val errorMessage = messages["player.streamError"].msgFormat(it.info().mrl())
                    find<PlayerViewModel>().stateProperty.value = PlayerState.Error(errorMessage)
                }
            }
        }
    }

    override fun mediaMetaChanged(media: Media?, metaType: Meta?) {
        media?.meta()?.let {
            logger.info { "VLCMetaService retrieved MetaData: ${it.asMetaData()}" }
            if (it[Meta.NOW_PLAYING] != null
                && it[Meta.TITLE] != null
            ) {
                val metaData = StreamMetaData(
                    stationName = it[Meta.TITLE],
                    nowPlaying = it[Meta.NOW_PLAYING]
                        .replace("\r", "")
                        .replace("\n", "")
                )
                appEvent.streamMetaDataUpdates.onNext(metaData)
            }
        }
    }
}