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

package online.hudacek.fxradio.utils

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.HttpClientHolder
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.storage.ImageCache
import tornadofx.runLater

private val logger = KotlinLogging.logger {}

val defaultRadioLogo by lazy { Image(Config.Resources.waveIcon) }

/**
 * This method is used for custom downloading of station's logo
 * and storing it in cache directory
 *
 * It is using custom URLConnection with fake user-agent because some servers deny
 * response when no user agent is send
 *
 * In case of error Industry-Radio-Tower-icon static png file is used as station logo
 */
internal fun ImageView.createImage(station: Station) {
    this.image = defaultRadioLogo

    if (!station.isValid()) return

    if (ImageCache.has(station)) {
        this.image = ImageCache.get(station)
    } else {
        if (station.favicon.isNullOrEmpty()) {
            logger.debug { "Image for ${station.name} is null or empty" }
            return
        }

        //Download the image with OkHttp client
        station.favicon?.let { url ->
            HttpClientHolder.client.call(url,
                    { response ->
                        response.body()?.let {
                            ImageCache.save(station, it.byteStream())
                            runLater {
                                this.image = ImageCache.get(station)
                            }
                        }
                    },
                    {
                        logger.error { "Downloading failed for ${station.name} (${it::class} : ${it.localizedMessage}) " }
                    })
        }
    }
}