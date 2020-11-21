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
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KClass

/**
 * Api Client
 * ---------------------
 * Helper to construct Retrofit instance with custom HTTP client with
 * enabled logging and custom User-Agent string
 */
class ApiClient(private val baseUrl: String) : OkHttpHelper() {

    private val logger = KotlinLogging.logger {}

    //Retrofit instance
    private val client: Retrofit
        get() = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(httpClient)
                .build()

    //Helper to construct Retrofit service class
    fun <T : Any> create(service: KClass<T>): T {
        logger.debug { "Creating service: $service" }
        return client.create(service.java)
    }
}