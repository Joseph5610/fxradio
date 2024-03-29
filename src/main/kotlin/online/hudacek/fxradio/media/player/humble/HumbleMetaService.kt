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

package online.hudacek.fxradio.media.player.humble

import io.humble.video.Demuxer
import io.humble.video.KeyValueBag
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.util.Duration
import mu.KotlinLogging
import online.hudacek.fxradio.event.AppEvent
import online.hudacek.fxradio.media.StreamMetaData
import tornadofx.find

private val logger = KotlinLogging.logger {}

private const val STREAM_NOW_PLAYING_KEY = "StreamTitle"
private const val STREAM_STATION_NAME_KEY = "icy-name"

/**
 * Service that regularly fetches
 * new stream MetaData Information from [streamUrl]
 */
class HumbleMetaDataService(private var streamUrl: String = "") : ScheduledService<KeyValueBag>() {

    init {
        period = Duration.seconds(55.0) // Period between fetching data
        delay = Duration.seconds(10.0) // Initial delay
    }

    /**
     * Updates [streamUrl] and restarts the service
     */
    fun restartFor(streamUrl: String) {
        this.streamUrl = streamUrl
        restart()
    }

    override fun createTask(): Task<KeyValueBag> = FetchDataTask()
    inner class FetchDataTask : Task<KeyValueBag>() {

        private val appEvent = find<AppEvent>()

        override fun call(): KeyValueBag {
            require(streamUrl.isNotEmpty()) { "streamUrl should not be empty." }

            val deMuxer = Demuxer.make()
            deMuxer.open(streamUrl, null, false, true, null, null)
            val data = deMuxer.metaData
            deMuxer.correctlyClose()
            logger.info { "HumbleMetaService retrieved MetaData: $data" }
            return data
        }

        /**
         * Gets MetaData values from DeMuxer and fires event with the new data
         */
        override fun succeeded() {
            if (value.getValue(STREAM_STATION_NAME_KEY) != null && value.getValue(STREAM_NOW_PLAYING_KEY) != null) {
                // Send new MetaData event if stream metadata values are present
                val metaData = StreamMetaData(
                    stationName = value.getValue(STREAM_STATION_NAME_KEY),
                    nowPlaying = value.getValue(STREAM_NOW_PLAYING_KEY)
                )
                appEvent.streamMetaDataUpdates.onNext(metaData)
            }
        }

        override fun failed() = logger.error(exception) { "FetchDataTask failed." }
    }
}
