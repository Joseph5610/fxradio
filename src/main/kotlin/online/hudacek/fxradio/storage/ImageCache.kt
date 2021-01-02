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

import javafx.beans.property.Property
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.HttpClientHolder
import online.hudacek.fxradio.api.model.Station
import org.apache.commons.io.FileUtils
import tornadofx.observableListOf
import tornadofx.onChange
import tornadofx.runLater
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

private val defaultRadioLogo by lazy { Image(Config.Resources.waveIcon) }

object ImageCache {

    private val cacheBasePath: Path = Paths.get(Config.Paths.cacheDirPath)

    //If the downloading failed, store the station uuid here and
    //don't download the file again until next run of the app
    private val invalidStationUuids by lazy { observableListOf<String>() }

    private val Station.isInvalidImage: Boolean
        get() = invalidStationUuids.contains(stationuuid)

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
    fun has(station: Station) = Files.exists(cacheBasePath.resolve(station.stationuuid)) || station.isInvalidImage

    //Get image from cache
    fun get(station: Station): Image {
        if (station.isInvalidImage) return defaultRadioLogo
        val imagePath = cacheBasePath.resolve(station.stationuuid)
        return try {
            val image = Image(FileInputStream(imagePath.toFile()))
            if (image.isError) {
                logger.error { "Can't show image for ${station.name} (${image.exception.localizedMessage}) " }
                addInvalid(station)
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

    fun addInvalid(station: Station) = invalidStationUuids.add(station.stationuuid)
}

/**
 * This method is used for custom downloading of station's logo
 * and storing it in cache directory
 *
 *
 * In case of error defaultRadioLogo static png file is used as station logo
 */
internal fun Property<Station>.stationImage(view: ImageView) {
    value.stationImage(view)

    onChange {
        it?.stationImage(view)
    }
}

internal fun Station.stationImage(view: ImageView) {
    view.image = defaultRadioLogo

    if (ImageCache.has(this)) {
        view.image = ImageCache.get(this)
    } else {
        if (favicon.isNullOrEmpty()) {
            ImageCache.addInvalid(this)
            logger.debug { "Image for $name is null or empty" }
            return
        }

        //Download the image with OkHttp client
        favicon?.let { url ->
            HttpClientHolder.client.call(url,
                    { response ->
                        response.body()?.let { body ->
                            try {
                                ImageCache.save(this, body.byteStream())
                            } catch (e: Exception) {
                                logger.error(e) { "Error while saving downloaded station image" }
                            }

                            runLater {
                                view.image = ImageCache.get(this)
                            }
                        }
                    },
                    { e ->
                        logger.error { "Downloading failed for $name (${this::class}: ${e.localizedMessage}) " }
                        ImageCache.addInvalid(this)
                    })
        }
    }
}