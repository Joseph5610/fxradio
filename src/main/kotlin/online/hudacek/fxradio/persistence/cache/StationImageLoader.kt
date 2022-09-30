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

import io.reactivex.Maybe
import io.reactivex.Single
import javafx.scene.image.Image
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.apiclient.stations.model.Station
import online.hudacek.fxradio.util.applySchedulers
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class StationImageLoader {

    private val cacheBasePath: Path = Paths.get(Config.Paths.cacheDirPath)

    fun load(station: Station): Maybe<Image?> = maybeOfNullable(getLocalPath(station))
            .onErrorResumeNext(getRemote(station))
            .switchIfEmpty(getRemote(station))

    private fun getRemote(station: Station): Maybe<Image> =
            maybeOfNullable(station.favicon)
                    .flatMapSingle {
                        if (it.isNotEmpty()) {
                            Single.fromCallable { HttpClient.request(it) }.compose(applySchedulers())
                        } else {
                            Single.error(IllegalArgumentException(
                                    "Station ${station.stationuuid} does not have valid icon URL"))
                        }
                    }.flatMapMaybe { response ->
                        maybeOfNullable(response.body())
                                .map { copyInputStreamIntoFile(it.byteStream(), station.stationuuid) }
                    }.flatMap {
                        maybeOfNullable(getLocalPath(station))
                    }

    /**
     * Gets Image for [station] from local cache
     */
    private fun getLocalPath(station: Station) = if (station.isCached) {
        Image("file:" + cacheBasePath.resolve(station.stationuuid).toFile().absolutePath, true)
    } else {
        null
    }

    private fun <T> maybeOfNullable(value: T?): Maybe<T> {
        return if (value == null) Maybe.empty() else Maybe.just(value)
    }

    private fun copyInputStreamIntoFile(ips: InputStream, fileName: String) {
        Files.copy(
                ips,
                cacheBasePath.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING)
    }

    private val Station.isCached: Boolean
        get() = Files.exists(cacheBasePath.resolve(stationuuid))
}