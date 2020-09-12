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
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import online.hudacek.fxradio.FxRadio
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

class ApiClient(private val baseUrl: String) {

    private val connectionPool = ConnectionPool(5, 20, TimeUnit.SECONDS)

    //What is app sending as a User Agent string
    private val userAgent = "${FxRadio.appName}/${FxRadio.version}"

    //Construct http client with custom user agent
    private val httpClient by lazy {
        OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    chain.proceed(
                            chain.request()
                                    .newBuilder()
                                    .header("User-Agent", userAgent)
                                    .build()
                    )
                }
                .connectionPool(connectionPool)
                .build()
    }

    fun build(): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(httpClient)
                .build()
    }

    fun shutdown() {
        logger.debug { "Shutting down okhttp" }
        logger.debug { "Idle: ${connectionPool.idleConnectionCount()} All: ${connectionPool.connectionCount()}" }
        httpClient.dispatcher().executorService().shutdown()
        httpClient.connectionPool().evictAll()
    }
}

internal fun <T : Any> ApiClient.create(service: KClass<T>): T {
    logger.debug { "Creating service: $service" }
    return this.build().create(service.java)
}