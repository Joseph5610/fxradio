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

package online.hudacek.fxradio.ui

import javafx.beans.property.Property
import javafx.scene.CacheHint
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.apiclient.stations.model.Station
import online.hudacek.fxradio.storage.ImageCache
import online.hudacek.fxradio.storage.ImageCache.isCached
import online.hudacek.fxradio.util.applySchedulers
import tornadofx.onChange

private val defaultRadioLogo by lazy { Image(Config.Resources.waveIcon) }

private val logger = KotlinLogging.logger {}

/**
 * This method is used for custom downloading of station's logo
 * and storing it in cache directory
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
    // Set basic image properties
    view.image = defaultRadioLogo
    view.isCache = true
    view.cacheHint = CacheHint.SPEED
    view.isPreserveRatio = true

    // If the image is in the cache, just load it into view
    if (isCached) {
        loadImage(view)
    } else {
        if (favicon.isNullOrEmpty()) {
            // Ignore invalid image and add it to list of invalid stations to not load it again
            ImageCache.addInvalid(this)
            return
        }

        // Download the image with OkHttp client
        favicon?.let { url ->
            HttpClient.request(url,
                    {
                        ImageCache.save(this, it)
                        loadImage(view)
                    },
                    {
                        ImageCache.addInvalid(this)
                    })
        }
    }
}

/**
 * Loads image of [Station] from cache into [view] asynchronously
 */
private fun Station.loadImage(view: ImageView) {
    ImageCache
            .get(this)
            .compose(applySchedulers())
            .subscribe({
                view.image = it
                it.errorProperty().onChange { isError ->
                    if (isError) {
                        view.image = defaultRadioLogo
                        ImageCache.addInvalid(this)
                    }
                }
            }, {
                logger.error(it) { "Exception when retrieving image from cache!" }
            })
}
