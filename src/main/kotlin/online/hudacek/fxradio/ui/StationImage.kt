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
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.cache.StationImageLoader
import tornadofx.onChange

private val defaultRadioLogo by lazy { Image(Config.Resources.waveIcon) }

private val logger = KotlinLogging.logger {}

private val loader = StationImageLoader()


/**
 * This method is used for custom downloading of station's logo
 * and storing it in cache directory
 *
 * In case of error defaultRadioLogo static png file is used as station logo
 */
internal fun ImageView.bindStation(stationProperty: Property<Station>) {
    stationProperty.value.stationImage(this)
    stationProperty.onChange {
        it?.stationImage(this)
    }
}

/**
 * Loads image of [Station] from cache into [view] asynchronously
 */
internal fun Station.stationImage(view: ImageView) {
    // Set basic image properties
    view.image = defaultRadioLogo
    view.isCache = true
    view.cacheHint = CacheHint.SPEED
    view.isPreserveRatio = true

    loader.load(this)
        .subscribe({
            view.image = it
            it?.errorProperty()?.onChange { isError ->
                if (isError) {
                    view.image = defaultRadioLogo
                }
            }
        }, {
            logger.trace { "Failed to load image: ${it.message}" }
        })
}
