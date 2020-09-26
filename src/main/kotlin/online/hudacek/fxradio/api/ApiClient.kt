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
import okhttp3.logging.HttpLoggingInterceptor
import online.hudacek.fxradio.FxRadio
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * Api Client
 * ---------------------
 * Helper to construct Retrofit instance with custom HTTP client with
 * enabled logging and custom User-Agent string
 */
class ApiClient(private val baseUrl: String) {

    private val logger = KotlinLogging.logger {}

    //To Limit the active connections
    private val connectionPool = ConnectionPool(5, 20, TimeUnit.SECONDS)

    //What is app sending as a User Agent string
    private val userAgent = "${FxRadio.appName}/${FxRadio.version}"

    //Logging of http requests
    private val loggerInterceptor = HttpLoggingInterceptor { message -> logger.debug { message } }

    //Construct http client with custom user agent
    private val httpClient by lazy {
        //Set logging level for HTTP requests to full request and response
        //Http requests are logged only on debug app logger level
        loggerInterceptor.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    chain.proceed(
                            chain.request()
                                    .newBuilder()
                                    .header("User-Agent", userAgent)
                                    .build()
                    )
                }
                .addInterceptor(loggerInterceptor)
                .connectionPool(connectionPool)
                .build()
    }

    fun build(): Retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(httpClient)
            .build()

    //Helper to construct Retrofit service class
    fun <T : Any> create(service: KClass<T>): T {
        logger.debug { "Creating service: $service" }
        return build().create(service.java)
    }

    fun shutdown() {
        logger.info { "Shutting down OKHttp" }
        logger.debug { "Idle: ${connectionPool.idleConnectionCount()} All: ${connectionPool.connectionCount()}" }
        //Kill all OkHttp Threads
        httpClient.dispatcher().executorService().shutdown()
        httpClient.connectionPool().evictAll()
    }
}