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

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javafx.scene.image.Image
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.ui.defaultRadioLogo
import org.apache.commons.io.FileUtils
import tornadofx.observableListOf
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

private val logger = KotlinLogging.logger {}

/**
 * Simple image cache
 * Images are saved according to their station id
 * and loaded using fileInputStream
 */
object ImageCache {

    private val cacheBasePath: Path = Paths.get(Config.Paths.cacheDirPath)

    //If the downloading failed, store the station uuid here and
    //don't download the file again until next run of the app
    private val invalidStationUuids = observableListOf<String>()

    init {
        //prepare cache directory
        if (!Files.isDirectory(cacheBasePath)) {
            Files.createDirectories(cacheBasePath)
        }
    }

    /**
     * Counts total size of cache directory in MB
     */
    val totalSize
        get() = (cacheBasePath.toFile()
                .walkTopDown().filter { it.isFile }
                .map { it.length() }
                .sum() / 1024).toInt()

    fun clear() = FileUtils.cleanDirectory(cacheBasePath.toFile())

    /**
     * Check if [station] image is in cache
     */
    fun has(station: Station) = Files.exists(cacheBasePath.resolve(station.stationuuid))
            || invalidStationUuids.contains(station.stationuuid)

    /**
     * Gets image for [station] from local cache
     */
    fun get(station: Station): Single<Image> = Single
            .just(cacheBasePath.resolve(station.stationuuid))
            .flatMap {
                Single.just(Image("file:" + it.toFile().absolutePath, true))
            }
            .doOnError { logger.error(it) { "Failed to get Image from cached file" } }
            .onErrorReturnItem(defaultRadioLogo)

    /**
     * Saves [inputStream] containing logo of [station] into local cache dir
     */
    fun save(station: Station, inputStream: InputStream): Disposable = Single.just(station)
            .filter { !has(it) }
            .doOnError { logger.error(it) { "Cannot save downloaded image" } }
            .map { cacheBasePath.resolve(it.stationuuid) }
            .subscribe {
                Files.copy(
                        inputStream,
                        it,
                        StandardCopyOption.REPLACE_EXISTING)
            }

    /**
     * Adds [station] into list of invalid stations
     * Means that image for given station is invalid / not supported by renderer or URL is invalid
     */
    fun addInvalid(station: Station) {
        if (!invalidStationUuids.contains(station.stationuuid)) {
            logger.debug { "Image for ${station.name} is added as invalid" }
            invalidStationUuids.add(station.stationuuid)
        }
    }
}