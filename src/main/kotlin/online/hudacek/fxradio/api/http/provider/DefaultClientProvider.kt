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

package online.hudacek.fxradio.api.http.provider

import mu.KotlinLogging
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import online.hudacek.fxradio.api.http.interceptor.UserAgentInterceptor
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

/**
 * Defines Connection timeout for the duration og w in seconds
 */
private const val timeoutInSeconds: Long = 20

/**
 * Helper variables and methods for OkHttpClient
 */
open class DefaultClientProvider : HttpClientProvider {

    /**
     * Defines the limits for the active connections
     */
    private val connectionPool = ConnectionPool(5, timeoutInSeconds, TimeUnit.SECONDS)

    /**
     * Enables Logging of http requests in app logger on debug level
     */
    private val loggerInterceptor = HttpLoggingInterceptor { message -> logger.trace { message } }
            .apply {
                //Set logging level for HTTP requests to full request and response
                //Http requests are logged only on trace app logger level
                level = HttpLoggingInterceptor.Level.BODY

                //Do not show sensitive information
                redactHeader("Authorization")
                redactHeader("Cookie")
            }

    override val interceptors: MutableList<Interceptor> = mutableListOf(loggerInterceptor)

    /**
     * Construct http client with custom user agent, connection pool and timeouts
     */
    override val client: OkHttpClient =
            OkHttpClient.Builder()
                    //The whole call should not take longer than 20 secs
                    .callTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                    .addNetworkInterceptor(UserAgentInterceptor())
                    .connectionPool(connectionPool)
                    .apply {
                        interceptors.forEach {
                            addInterceptor(it)
                        }
                    }.build()

    /**
     * Correctly kill all OkHttpClient threads
     */
    override fun close() {
        logger.info { "Closing OkHttpClient..." }
        logger.debug { "Idle/All: ${connectionPool.idleConnectionCount()}/${connectionPool.connectionCount()}" }
        super.close()
    }
}