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

package online.hudacek.fxradio.media.player.humble

import io.humble.video.Demuxer
import io.humble.video.KeyValueBag
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.util.Duration
import mu.KotlinLogging
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.media.StreamMetaData
import tornadofx.find

private val logger = KotlinLogging.logger {}

/**
 * Service that regularly fetches
 * new stream MetaData Information from [streamUrl]
 */
class HumbleMetaDataService(private var streamUrl: String = "") : ScheduledService<KeyValueBag>() {

    init {
        period = Duration.seconds(50.0) //period between fetching data
        delay = Duration.seconds(5.0) //initial delay
    }

    /**
     * Updates [streamUrl] and restarts the service
     */
    fun restartFor(streamUrl: String) {
        this.streamUrl = streamUrl
        restart()
    }

    override fun createTask(): Task<KeyValueBag> = FetchDataTask()

    private inner class FetchDataTask : Task<KeyValueBag>() {
        private val streamNowPlayingKey = "StreamTitle"
        private val streamStationName = "icy-name"

        private val appEvent = find<AppEvent>()

        override fun call(): KeyValueBag {
            if (streamUrl.isEmpty()) throw IllegalArgumentException("streamUrl should not be empty.")

            val deMuxer = Demuxer.make()
            deMuxer.open(streamUrl, null, false,
                    true, null, null)
            val data = deMuxer.metaData
            deMuxer.close()
            logger.debug { "FetchDataTask: $data" }
            return data
        }

        /**
         * Gets MetaData values from DeMuxer and fires event with the new data
         */
        override fun succeeded() {
            if (value.getValue(streamStationName) != null
                    && value.getValue(streamNowPlayingKey) != null) {
                //Send new MetaData event if stream metadata values are present
                val mediaMeta = StreamMetaData(value.getValue(streamStationName), value.getValue(streamNowPlayingKey))
                appEvent.streamMetaData.onNext(mediaMeta)
            }
        }

        override fun failed() = logger.error(exception) { "FetchDataTask failed." }
    }
}