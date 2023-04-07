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

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import javafx.scene.CacheHint
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import mu.KotlinLogging
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.cache.StationImageCache
import online.hudacek.fxradio.util.toObservable

private val logger = KotlinLogging.logger {}

/**
 * Custom ImageView for station favicons
 */
class StationImageView(
    private val stationObservable: Observable<Station>,
    size: Double = 15.0,
) : ImageView(stationImageCache.defaultStationLogo) {

    private val imagePropertyObservable = imageProperty().toObservable()

    init {
        // Set basic image properties
        isCache = true
        cacheHint = CacheHint.SPEED
        isPreserveRatio = true
        fitWidth = size
        fitHeight = size

        // Handles cases when image is correct but JavaFX is unable to render it
        imagePropertyObservable.flatMap { it.errorProperty().toObservable() }
            .subscribe { isError ->
                if (isError) {
                    logger.trace { "Failed to set image: ${image.exception.message}" }
                    image = stationImageCache.defaultStationLogo
                }
            }
    }

    /**
     * Maps currently playing station to its logo from cache
     */
    val imageObservable: Observable<Image> by lazy {
        stationObservable.flatMapMaybe { stationImageCache.load(it) }
    }

    /**
     * Subscribe to start emits of station logos
     */
    fun subscribe(): Disposable = imageObservable.subscribe({
        image = it
    }, {
        image = stationImageCache.defaultStationLogo
        logger.trace { "Failed to load image: ${it.message}" }
    })

    companion object {
        private val stationImageCache by lazy { StationImageCache() }
    }
}
