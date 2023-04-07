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

package online.hudacek.fxradio.persistence.cache

import io.reactivex.rxjava3.core.Maybe
import javafx.scene.image.Image
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.walk
import kotlin.math.roundToInt

private val logger = KotlinLogging.logger {}

private const val MAX_CACHED_WEEKS = 3L

/**
 * Simple image cache used for station images
 * Files are saved to /.fxradio/cache/ directory
 * File name is the station UUID
 */
@OptIn(ExperimentalPathApi::class)
abstract class ImageCache {

    abstract fun load(station: Station): Maybe<Image>

    companion object {

        internal val cacheBasePath: Path = Paths.get(Config.Paths.cacheDirPath)

        init {
            // Prepare cache directory
            createCacheDirectory()

            // Delete too old images
            removeOldRecords()
        }

        /**
         * Removes cache directory with all its contents and recreates it afterward
         */
        fun clear() = cacheBasePath.deleteRecursively().also { createCacheDirectory() }

        /**
         * Counts total size of cache directory in MB
         */
        val totalSize: Int
            get() = (cacheBasePath
                .walk()
                .map { it.fileSize() }
                .sum() / 1e+6).roundToInt()

        val Station.isCached: Boolean
            get() = cacheBasePath.resolve(uuid).exists()

        private fun createCacheDirectory() {
            if (!cacheBasePath.isDirectory()) {
                cacheBasePath.createDirectories()
            }
        }

        /**
         * Removes every cached image older than [MAX_CACHED_WEEKS]
         */
        private fun removeOldRecords() {
            logger.debug { "Clearing old image cache records..." }
            val cut = LocalDateTime.now().minusWeeks(MAX_CACHED_WEEKS).toEpochSecond(ZoneOffset.UTC)
            cacheBasePath.walk()
                .filter { it.getLastModifiedTime().to(TimeUnit.SECONDS) < cut }
                .forEach {
                    runCatching {
                        it.deleteIfExists()
                    }.onFailure { logger.error(it) { "Failed to remove record" } }
                }
        }
    }
}
