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

import javafx.beans.property.StringProperty
import okhttp3.*
import java.io.IOException

/**
 * Basic async http client
 * -----------------------------
 *
 */
class BasicHttpClient(url: String) {

    constructor(url: StringProperty) : this(url.value)

    private val client by lazy { OkHttpClient() }

    private val request: Request by lazy {
        Request.Builder()
                .url(url)
                .build()
    }

    fun call(success: Response.() -> Unit = {}, fail: IOException.() -> Unit = {}) = client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    fail.invoke(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    success.invoke(response)
                }
            }
    )
}