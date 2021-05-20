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

package online.hudacek.fxradio.ui

import javafx.beans.property.Property
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.http.HttpClient
import online.hudacek.fxradio.api.stations.model.Station
import online.hudacek.fxradio.storage.ImageCache
import online.hudacek.fxradio.storage.ImageCache.isCached
import tornadofx.onChange

val defaultRadioLogo by lazy { Image(Config.Resources.waveIcon) }

private val logger = KotlinLogging.logger {}

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

    //If the image is in the cache, just load it into view
    if (isCached) {
        loadImage(view)
    } else {
        if (favicon.isNullOrEmpty()) {
            //Ignore invalid image and add it to list of invalid stations to not load it again
            ImageCache.addInvalid(this)
            return
        }

        //Download the image with OkHttp client
        favicon?.let { url ->
            HttpClient.request(url,
                    { response ->
                        response.body()?.let { body ->
                            ImageCache.save(this, body.byteStream())
                            loadImage(view)
                        }
                    },
                    {
                        ImageCache.addInvalid(this)
                    })
        }
    }
}

//Load image into view asynchronously
private fun Station.loadImage(view: ImageView) {
    ImageCache
            .get(this)
            .subscribe({
                view.image = it
                it.errorProperty().onChange { isError ->
                    if (isError) {
                        view.image = defaultRadioLogo
                        ImageCache.addInvalid(this)
                    }
                }
            }, {
                logger.error(it) { "Exception when loading image from cache!" }
            })
}