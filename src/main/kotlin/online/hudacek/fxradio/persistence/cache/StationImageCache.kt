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

package online.hudacek.fxradio.persistence.cache

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javafx.scene.image.Image
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.util.applySchedulersMaybe
import online.hudacek.fxradio.util.maybeOfNullable
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

class StationImageCache : ImageCache() {

    private val defaultStationLogoMaybe by lazy { Maybe.just(defaultStationLogo) }

    /**
     * Retrieves the station image either from local cache or remote url
     */
    override fun load(station: Station): Maybe<Image> = maybeOfNullable(getLocalPath(station))
        .switchIfEmpty(getRemote(station))
        .switchIfEmpty(defaultStationLogoMaybe) // If favicon is null, use default
        .compose(applySchedulersMaybe())
        .onErrorResumeNext { defaultStationLogoMaybe }

    /**
     * Downloads and stores remote image file into local cache
     */
    private fun getRemote(station: Station): Maybe<Image> =
        maybeOfNullable(station.favicon)
            .flatMapSingle {
                if (it.isNotEmpty()) {
                    Single.fromCallable { HttpClient.request(it) }
                } else {
                    Single.error(
                        IllegalArgumentException("Station ${station.uuid} does not have valid icon URL")
                    )
                }
            }.flatMap { response ->
                maybeOfNullable(response.body)
                    .map { copyInputStreamIntoFile(it.byteStream(), station.uuid) }
            }.flatMap {
                maybeOfNullable(getLocalPath(station))
            }

    /**
     * Gets Image for [station] from local cache
     */
    private fun getLocalPath(station: Station): Image? {
        val stationImagePath = cacheBasePath.resolve(station.uuid)
        return if (stationImagePath.exists()) {
            Image("file:" + stationImagePath.absolutePathString(), true)
        } else {
            null
        }
    }

    companion object {

        val defaultStationLogo by lazy { Image(Config.Resources.APP_WAVE_ICON) }

        private fun copyInputStreamIntoFile(ips: InputStream, fileName: String) = ips.use {
            Files.copy(it, cacheBasePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}
