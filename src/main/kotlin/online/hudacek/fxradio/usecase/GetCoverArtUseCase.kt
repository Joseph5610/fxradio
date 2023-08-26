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

package online.hudacek.fxradio.usecase

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import mu.KotlinLogging
import okhttp3.Response
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.MusicBrainzApiProvider
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.apiclient.musicbrainz.MusicBrainzApi
import online.hudacek.fxradio.apiclient.musicbrainz.model.Release
import online.hudacek.fxradio.util.applySchedulersMaybe
import online.hudacek.fxradio.util.maybeOfNullable

private val logger = KotlinLogging.logger {}

private const val SCORE_THRESHOLD = 95
private const val ART_PATH = "/front-250"

/**
 * Retrieve Cover Art for currently playing song (if stream metadata is provided)
 */
class GetCoverArtUseCase : BaseUseCase<String, Maybe<Response>>() {

    private val musicBrainzApi: MusicBrainzApi by lazy { MusicBrainzApiProvider.provide() }

    override fun execute(input: String): Maybe<Response> = musicBrainzApi.getReleases(input)
        .flatMapMaybe { maybeOfNullable(it.releases.firstOrNull { r -> r.score >= SCORE_THRESHOLD }) }
        .map {
            // Take only the most probable candidate for cover art
            val coverUrl = Config.API.coverArtApiUrl + it.id + ART_PATH
            ReleaseWithCoverArt(coverUrl, it)
        }
        .doOnSuccess { logger.debug { "Requesting CoverArt: ${it.coverArtUrl}" } }
        .flatMapSingle { Single.fromCallable { HttpClient.request(it.coverArtUrl) } }
        .compose(applySchedulersMaybe())
        .onErrorComplete()
}

data class ReleaseWithCoverArt(val coverArtUrl: String, val release: Release)