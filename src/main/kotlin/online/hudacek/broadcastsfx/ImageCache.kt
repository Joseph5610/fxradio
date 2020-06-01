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

package online.hudacek.broadcastsfx

import javafx.scene.image.Image
import mu.KotlinLogging
import online.hudacek.broadcastsfx.extension.defaultRadioLogo
import online.hudacek.broadcastsfx.model.rest.Station
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Simple image cache
 * Images are saved according to their station id
 * and loaded using fileInputStream
 */
object ImageCache {
    private val cacheBasePath: Path = Paths.get(Config.Paths.imageCache)
    private val logger = KotlinLogging.logger {}

    init {
        //prepare cache directory
        if (!Files.isDirectory(cacheBasePath)) {
            logger.debug { "init cache directory" }
            Files.createDirectories(cacheBasePath)
        }
    }

    fun clearCache(): Boolean {
        return try {
            FileUtils.cleanDirectory(cacheBasePath.toFile())
            true
        } catch (e: IOException) {
            logger.error(e) {
                "IOException when clearing cache"
            }
            false
        }
    }

    fun isImageInCache(station: Station): Boolean {
        val imagePath = cacheBasePath.resolve(station.stationuuid)
        return Files.exists(imagePath)
    }

    fun getImageFromCache(station: Station): Image {
        val imagePath = cacheBasePath.resolve(station.stationuuid)
        val image = Image(FileInputStream(imagePath.toFile()))
        return if (image.isError) {
            logger.error {
                "image showing failed for ${station.name} (${image.exception.localizedMessage}) "
            }
            defaultRadioLogo
        } else {
            image
        }
    }

    fun getImageFromCacheAsFile(station: Station): File {
        val imagePath = cacheBasePath.resolve(station.stationuuid)
        val file = imagePath.toFile()
        return if (!file.exists()) {
            File(URI.create(javaClass.classLoader.getResource(Config.Paths.defaultRadioIcon).toString()))
        } else {
            file
        }
    }

    fun saveImage(station: Station, inputStream: InputStream) {
        if (isImageInCache(station)) return
        val imagePath = cacheBasePath.resolve(station.stationuuid)
        try {
            Files.copy(
                    inputStream,
                    imagePath,
                    StandardCopyOption.REPLACE_EXISTING)
        } catch (e: FileAlreadyExistsException) {
            logger.error(e) {
                "FileAlreadyExists. Probably downloaded on some different thread?"
            }
        }
    }
}