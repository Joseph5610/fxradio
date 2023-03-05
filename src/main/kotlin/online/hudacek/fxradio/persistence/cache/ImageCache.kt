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
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.math.roundToInt

/**
 * Simple image cache used for station images
 * Files are saved to /.fxradio/cache/ directory
 * File name is the station UUID
 */
abstract class ImageCache {

    abstract fun load(station: Station): Maybe<Image>

    companion object {

        internal val cacheBasePath: Path = Paths.get(Config.Paths.cacheDirPath)

        init {
            // Prepare cache directory
            createCacheDirectory()
        }

        /**
         * Removes cache directory with all its contents and recreates it afterwards
         */
        fun clear(): Boolean = cacheBasePath.toFile().deleteRecursively().also { createCacheDirectory() }

        /**
         * Counts total size of cache directory in MB
         */
        val totalSize: Int
            get() = (cacheBasePath.toFile()
                .walkTopDown().filter { it.isFile }
                .map { it.length() }
                .sum() / 1e+6).roundToInt()

        val Station.isCached: Boolean
            get() = Files.exists(cacheBasePath.resolve(uuid))

        private fun createCacheDirectory() {
            if (!Files.isDirectory(cacheBasePath)) {
                Files.createDirectories(cacheBasePath)
            }
        }
    }
}
