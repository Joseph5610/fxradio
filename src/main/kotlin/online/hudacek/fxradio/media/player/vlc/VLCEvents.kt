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

import io.reactivex.subjects.BehaviorSubject
import mu.KotlinLogging
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.media.StreamMetaData
import online.hudacek.fxradio.media.StreamUnavailableException
import tornadofx.Controller
import tornadofx.get
import uk.co.caprica.vlcj.log.LogEventListener
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.Meta
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter

private val logger = KotlinLogging.logger {}

class VLCEvents : Controller() {

    private val appEvent: AppEvent by inject()
    private var lastLogMessage = ""

    val endPlayingEvent = BehaviorSubject.create<Unit>()

    /**
     * Listen for VLC native logs and print them to our logger
     */
    val nativeLogListener = LogEventListener { level, module, _, _, name, _, _, message ->
        lastLogMessage = message
        logger.debug { "[$module] ($name) $level: $message" }
    }

    /**
     * Adapter that registers changes of playing status in VLC and sends it to AudioComponent
     */
    val mediaStatusAdapter = object : MediaPlayerEventAdapter() {
        override fun finished(mediaPlayer: uk.co.caprica.vlcj.player.base.MediaPlayer?) {
            end(0)
        }

        override fun error(mediaPlayer: uk.co.caprica.vlcj.player.base.MediaPlayer?) {
            end(1)
        }

        /**
         * Called on libVLC event thread
         */
        private fun end(result: Int) {
            logger.debug { "Ending current stream with status: $result" }

            if (result == 1) {
                val errorMsg = if (lastLogMessage.isEmpty()) {
                    messages["player.streamError"]
                } else {
                    lastLogMessage
                }
                throw StreamUnavailableException(errorMsg)
            }
            endPlayingEvent.onNext(Unit)
        }
    }

    /**
     * Adapter that handles changes of metaData in VLC and sends it to App playerMetaData event
     */
    val mediaMetaDataAdapter = object : MediaEventAdapter() {
        override fun mediaMetaChanged(media: Media?, metaType: Meta?) {
            media?.meta()?.let {
                if (it[Meta.NOW_PLAYING] != null
                        && it[Meta.TITLE] != null) {
                    val metaData = StreamMetaData(it[Meta.TITLE],
                            it[Meta.NOW_PLAYING]
                                    .replace("\r", "")
                                    .replace("\n", ""))
                    appEvent.streamMetaData.onNext(metaData)
                }
            }
        }
    }
}