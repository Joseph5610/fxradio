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

package online.hudacek.fxradio.apiclient.http.provider

import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress

abstract class HttpClientProvider {

    abstract val client: OkHttpClient

    protected abstract val interceptors: List<Interceptor>

    /**
     * Default implementation of closing the OkHttpClient
     */
    fun close() {
        client.dispatcher().executorService().shutdownNow()
        client.connectionPool().evictAll()
    }

    fun dns(hostname: String): MutableList<InetAddress> = client.dns().lookup(hostname)

    fun request(url: String): Call = client.newCall(buildRequest(url))

    /**
     * Constructs [Request] object for given [url] address
     */
    private fun buildRequest(url: String) = Request.Builder()
            .url(url)
            .build()
}