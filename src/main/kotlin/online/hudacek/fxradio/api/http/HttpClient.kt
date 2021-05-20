/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.api.http

import mu.KotlinLogging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import online.hudacek.fxradio.api.http.providers.DefaultClientProvider
import java.io.IOException
import java.net.InetAddress

private val logger = KotlinLogging.logger {}

/**
 * Creates and holds single instance of OkHttpClient
 * Plain OkHttpClient is used mostly for downloading images of stations
 */
object HttpClient {

    //Uses default HTTP client
    private val clientProvider = DefaultClientProvider()

    /**
     * Performs DNS lookup for [address]
     */
    fun lookup(address: String): MutableList<InetAddress> {
        return clientProvider.client.dns().lookup(address).apply {
            logger.debug { "DNS lookup for $address returned $this" }
        }
    }

    /**
     * Performs HTTP request for [url]
     */
    fun request(url: String,
                success: (Response) -> Unit,
                fail: (IOException) -> Unit) = clientProvider.client.newCall(buildRequest(url)).enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    success(response)
                    response.close()
                }

                override fun onFailure(call: Call, e: IOException) {
                    logger.error { "Request to $url failed." }
                    logger.trace(e) { "Request to $url failed." }
                    fail(e)
                }
            }
    )

    /**
     * Constructs [Request] object for given [url] address
     */
    private fun buildRequest(url: String) = Request.Builder()
            .url(url)
            .build()

    fun close() = clientProvider.close()
}