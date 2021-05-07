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

private val logger = KotlinLogging.logger {}

/**
 * Helper variables and methods for OkHttpClient
 */
open class OkHttpHelper {

    /**
     * Defines the limits for the active connections
     */
    private val connectionPool = ConnectionPool(5, 20, TimeUnit.SECONDS)

    /**
     * Defines what is app sending as a User Agent string
     */
    private val userAgent = "${FxRadio.appName}/${FxRadio.version}"

    /**
     * Enables Logging of http requests in app logger on debug level
     */
    private val loggerInterceptor = HttpLoggingInterceptor { message -> logger.trace { message } }
            .apply {
                //Set logging level for HTTP requests to full request and response
                //Http requests are logged only on trace app logger level
                level = HttpLoggingInterceptor.Level.BODY
            }

    /**
     * Construct http client with custom user agent, connection pool and timeouts
     */
    protected val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
                //The whole call should not take longer than 20 secs
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

    /**
     * Correctly kill all OkHttpClient threads
     */
    fun shutdown() {
        logger.info { "Shutting down OkHttpClient $httpClient" }
        logger.debug {
            "Idle connections: ${connectionPool.idleConnectionCount()} " +
                    "All connections: ${connectionPool.connectionCount()}"
        }
        httpClient.dispatcher().executorService().shutdownNow()
        httpClient.connectionPool().evictAll()
    }
}