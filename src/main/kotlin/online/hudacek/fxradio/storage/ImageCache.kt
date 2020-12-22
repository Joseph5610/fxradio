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

package online.hudacek.fxradio.storage

import javafx.scene.image.Image
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.utils.defaultRadioLogo
import org.apache.commons.io.FileUtils
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.math.round

/**
 * Simple image cache
 * Images are saved according to their station id
 * and loaded using fileInputStream
 */
private val logger = KotlinLogging.logger {}

object ImageCache {

    private val cacheBasePath: Path = Paths.get(Config.Paths.cacheDirPath)

    init {
        //prepare cache directory
        if (!Files.isDirectory(cacheBasePath)) {
            Files.createDirectories(cacheBasePath)
        }
    }

    val totalSize
        get() = round((cacheBasePath.toFile().walkTopDown().filter { it.isFile }.map { it.length() }.sum() / 1024).toDouble())

    fun clear() = FileUtils.cleanDirectory(cacheBasePath.toFile())

    //Check if Station is in cache
    fun has(station: Station) = Files.exists(cacheBasePath.resolve(station.stationuuid))

    //Get image from cache
    fun get(station: Station): Image {
        val imagePath = cacheBasePath.resolve(station.stationuuid)
        return try {
            val image = Image(FileInputStream(imagePath.toFile()))
            if (image.isError) {
                logger.error { "Can't show image for ${station.name} (${image.exception.localizedMessage}) " }
                defaultRadioLogo
            } else {
                image
            }
        } catch (e: FileNotFoundException) {
            defaultRadioLogo
        }
    }

    @Throws(java.nio.file.FileAlreadyExistsException::class)
    fun save(station: Station, inputStream: InputStream) {
        if (has(station)) return
        val imagePath = cacheBasePath.resolve(station.stationuuid)
        Files.copy(
                inputStream,
                imagePath,
                StandardCopyOption.REPLACE_EXISTING)
    }
}