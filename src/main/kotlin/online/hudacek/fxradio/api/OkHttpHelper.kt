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
import java.util.concurrent.TimeUnit

open class OkHttpHelper {

    private val logger = KotlinLogging.logger {}

    //To Limit the active connections
    private val connectionPool = ConnectionPool(5, 20, TimeUnit.SECONDS)

    //What is app sending as a User Agent string
    private val userAgent = "${FxRadio.appName}/${FxRadio.version.version}"

    //Logging of http requests
    private val loggerInterceptor = HttpLoggingInterceptor { message -> logger.debug { message } }

    //Construct http client with custom user agent
    protected val httpClient: OkHttpClient by lazy {
        //Set logging level for HTTP requests to full request and response
        //Http requests are logged only on debug app logger level
        loggerInterceptor.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
                //The whole call should not take longer than 10 secs
                .callTimeout(20, TimeUnit.SECONDS)
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

    fun shutdown() {
        logger.info { "Shutting down OkHttpClient $httpClient" }
        logger.debug { "Idle: ${connectionPool.idleConnectionCount()} All: ${connectionPool.connectionCount()}" }
        //Kill all OkHttp Threads
        httpClient.dispatcher().executorService().shutdownNow()
        httpClient.connectionPool().evictAll()
    }
}