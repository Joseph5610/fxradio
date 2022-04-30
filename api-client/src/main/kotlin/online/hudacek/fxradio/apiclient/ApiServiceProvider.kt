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

package online.hudacek.fxradio.apiclient

import online.hudacek.fxradio.apiclient.http.provider.DefaultClientProvider
import online.hudacek.fxradio.apiclient.http.provider.HttpClientProvider
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides Retrofit instance for [baseUrl]
 */
class ApiServiceProvider(
    private val baseUrl: String,
    private val httpClientProvider: HttpClientProvider = DefaultClientProvider()
) {

    /**
     * Retrofit client instance for [baseUrl] with custom [httpClientProvider]
     */
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(httpClientProvider.client)
            .build()

    /**
     * Constructs Retrofit service class of type [T]
     */
    inline fun <reified T : Any> get(): T = retrofit.create(T::class.java)

    fun close() = httpClientProvider.close()
}