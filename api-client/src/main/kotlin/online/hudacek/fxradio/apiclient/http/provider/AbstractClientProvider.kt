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

package online.hudacek.fxradio.apiclient.http.provider

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress

/**
 * Base OkHttpClient provider
 */
abstract class AbstractClientProvider {

    abstract val client: OkHttpClient

    abstract val dnsClient: DnsOverHttps

    /**
     * Default implementation of closing the OkHttpClient
     */
    open fun close() {
        client.dispatcher.executorService.shutdownNow()
        client.connectionPool.evictAll()
    }

    fun dns(hostname: String): List<InetAddress> = runCatching {
        dnsClient.lookup(hostname).sortedBy { it.canonicalHostName }
    }.getOrDefault(emptyList())

    /**
     * Execute HTTP request to [url]
     */
    fun request(url: String): Response = client.newCall(buildRequest(url)).execute()

    /**
     * Constructs [Request] object for given [url] address
     */
    private fun buildRequest(url: String) = Request.Builder()
        .url(url)
        .build()
}
