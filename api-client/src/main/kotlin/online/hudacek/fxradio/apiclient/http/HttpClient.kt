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

package online.hudacek.fxradio.apiclient.http

import mu.KotlinLogging
import okhttp3.Response
import online.hudacek.fxradio.apiclient.http.provider.BasicClientProvider
import java.net.InetAddress

private val logger = KotlinLogging.logger {}

/**
 * Creates and holds single instance of OkHttpClient
 * Plain OkHttpClient is used mostly for downloading images of stations
 */
object HttpClient {

    // Uses default HTTP client
    private val clientProvider by lazy { BasicClientProvider() }

    /**
     * Performs DNS lookup for [address]
     */
    fun lookup(address: String): List<InetAddress> = clientProvider.dns(address)

    /**
     * Performs HTTP request for [url]
     */
    fun request(url: String): Response {
        logger.trace { "Performing request to $url" }
        return clientProvider.request(url)
    }

    fun close() = clientProvider.close()
}
