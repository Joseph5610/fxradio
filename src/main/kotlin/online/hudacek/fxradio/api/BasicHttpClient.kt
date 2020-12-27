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

package online.hudacek.fxradio.api

import mu.KotlinLogging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress

/**
 * Creates and holds single instance of OkHttpClient
 * Plain OkHttpClient is used mostly for downloading images of stations
 */
private val logger = KotlinLogging.logger {}


object HttpClientHolder {
    val client by lazy { BasicHttpClient() }
}

class BasicHttpClient : OkHttpHelper() {

    //Perform DNS lookup
    fun lookup(address: String): MutableList<InetAddress> {
        logger.debug { "Performing DNS lookup for $address" }
        return httpClient.dns().lookup(address)
    }

    fun call(url: String,
             success: (Response) -> Unit,
             fail: (IOException) -> Unit) = httpClient.newCall(request(url)).enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    success(response)
                    response.close()
                }

                override fun onFailure(call: Call, e: IOException) {
                    fail(e)
                }
            }
    )

    private fun request(url: String) = Request.Builder()
            .url(url)
            .build()
}