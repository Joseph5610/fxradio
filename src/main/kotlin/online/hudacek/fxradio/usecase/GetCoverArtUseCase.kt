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

import io.reactivex.rxjava3.core.Single
import okhttp3.Response
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.MusicBrainzApiProvider
import online.hudacek.fxradio.apiclient.http.HttpClient
import online.hudacek.fxradio.apiclient.musicbrainz.MusicBrainzApi
import online.hudacek.fxradio.apiclient.musicbrainz.model.Release
import online.hudacek.fxradio.util.applySchedulersSingle

private const val SCORE_THRESHOLD = 95

class GetCoverArtUseCase : BaseUseCase<String, Single<Response>>() {

    private val musicBrainzApi: MusicBrainzApi by lazy { MusicBrainzApiProvider.provide() }

    override fun execute(input: String): Single<Response> = musicBrainzApi.search(input)
        .map {
            val release = it.releases.first { r -> r.score >= SCORE_THRESHOLD }
            val coverUrl = Config.API.coverArtApiUrl + release.id + "/front-250"
            ReleaseWithCoverArt(coverUrl, release)
        }
        .flatMap{ Single.fromCallable { HttpClient.request(it.coverArtUrl) } }
        .compose(applySchedulersSingle())
}

data class ReleaseWithCoverArt(val coverArtUrl: String, val release: Release)