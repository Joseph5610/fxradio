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

package online.hudacek.broadcastsfx.extension

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import mu.KotlinLogging
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.*
import java.net.URL

private val logger = KotlinLogging.logger {}

internal val defaultRadioLogo by lazy { Image(Config.Paths.defaultRadioIcon) }

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

    if (!station.isValidStation()) return

    if (ImageCache.isImageInCache(station)) {
        this.image = ImageCache.getImageFromCache(station)
    } else {
        if (station.isInvalidImage()) {
            logger.info { "Image for ${station.name} is null or empty." }
            return
        }

        logger.debug { "downloading logo of ${station.name} from ${station.favicon}" }

        runAsync {
            URL(station.favicon).openConnection().apply {
                setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)")
                getInputStream().use { stream ->
                    ImageCache.saveImage(station, stream)
                }
            }
        } success {
            this.image = ImageCache.getImageFromCache(station)
        } fail {
            logger.error {
                "image download failed for ${station.name} (${it::class} : ${it.localizedMessage}) "
            }
            this.image = defaultRadioLogo
        }
    }
}