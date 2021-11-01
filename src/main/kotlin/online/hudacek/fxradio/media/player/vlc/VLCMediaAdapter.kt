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

package online.hudacek.fxradio.media.player.vlc

import mu.KotlinLogging
import online.hudacek.fxradio.event.AppEvent
import online.hudacek.fxradio.media.StreamMetaData
import online.hudacek.fxradio.media.StreamUnavailableException
import online.hudacek.fxradio.ui.formatted
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
            if (newState == State.ERROR || newState == State.ENDED) throw StreamUnavailableException(messages["player.streamError"].formatted(it.info().mrl()))
        }
    }

    override fun mediaMetaChanged(media: Media?, metaType: Meta?) {
        media?.meta()?.let {
            logger.debug { "VLCMetaService retrieved MetaData: ${it.asMetaData()}" }
            if (it[Meta.NOW_PLAYING] != null
                    && it[Meta.TITLE] != null) {
                val metaData = StreamMetaData(it[Meta.TITLE],
                        it[Meta.NOW_PLAYING]
                                .replace("\r", "")
                                .replace("\n", ""))
                appEvent.streamMetaDataUpdated.onNext(metaData)
            }
        }
    }
}