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

package online.hudacek.fxradio.ui.view

import javafx.beans.property.Property
import javafx.scene.CacheHint
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.cache.InvalidStationsHolder.hasInvalidLogo
import online.hudacek.fxradio.persistence.cache.InvalidStationsHolder.setInvalidLogo
import online.hudacek.fxradio.persistence.cache.StationImageCache
import online.hudacek.fxradio.util.toObservable
import tornadofx.objectProperty
import kotlin.math.roundToInt

private val logger = KotlinLogging.logger {}

/**
 * Custom ImageView for station logos
 */
class StationImageView(
    private val stationProperty: Property<Station>,
    size: Double = 15.0
) : ImageView(defaultRadioLogo) {

    constructor(station: Station, size: Double) : this(objectProperty(station), size)

    init {
        // Set basic image properties
        isCache = true
        cacheHint = CacheHint.SPEED
        isPreserveRatio = true
        fitWidth = size
        fitHeight = size
        image = defaultRadioLogo

        // Subscribe to property changes
        stationProperty.toObservable().subscribe {
            // Ignore images previously marked as invalid
            if (it.hasInvalidLogo()) {
                image = defaultRadioLogo
            } else {
                getStationImage()
            }
        }
    }

    /**
     * Loads favicon of [stationProperty] from cache into [image] field asynchronously
     */
    fun getStationImage() {
        stationImageCache.load(stationProperty.value).subscribe({
            image = it
            it.errorProperty()?.toObservable()?.subscribe { isError ->
                if (isError) {
                    logger.trace { "Failed to set image: ${it.exception.message}" }
                    image = defaultRadioLogo
                    stationProperty.value.setInvalidLogo()
                }
            }
        }, {
            stationProperty.value.setInvalidLogo()
            logger.trace { "Failed to load image: ${it.message}" }
        })
    }

    /**
     * Retrieve dominant color of current station image
     */
    fun getDominantColor(): Color {
        val pr = image.pixelReader
        val colCount: MutableMap<Color, Long> = hashMapOf()

        for (x in 0 until image.width.roundToInt()) {
            for (y in 0 until image.height.roundToInt()) {
                val col = pr.getColor(x, y)
                if (colCount.containsKey(col)) {
                    colCount[col] = colCount[col]!! + 1
                } else {
                    colCount[col] = 1L
                }
            }
        }
        return colCount.maxBy { it.value }.key
    }

    companion object {

        private val defaultRadioLogo by lazy { Image(Config.Resources.waveIcon) }

        private val stationImageCache by lazy { StationImageCache() }
    }
}
