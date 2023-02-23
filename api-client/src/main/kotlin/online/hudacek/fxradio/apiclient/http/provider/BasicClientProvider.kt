/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.apiclient.http.provider

import mu.KotlinLogging
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import online.hudacek.fxradio.apiclient.ApiUtils
import online.hudacek.fxradio.apiclient.http.interceptor.CacheInterceptor
import online.hudacek.fxradio.apiclient.http.interceptor.UserAgentInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

/**
 * Defines Connection timeout for the duration of call in seconds
 */
private const val TIMEOUT_SECS: Long = 45
private const val MAX_IDLE_CONNECTIONS: Int = 7
private const val MAX_CACHE_SIZE = 100L * 1024L * 1024L  // 100 MiB
private const val CACHE_BASE_DIR = "fxradio-http-cache"

/**
 * Base OkHttpClient implementation
 */
class BasicClientProvider : HttpClientProvider() {

    /**
     * Defines the limits for the active connections
     */
    private val connectionPool = ConnectionPool(MAX_IDLE_CONNECTIONS, TIMEOUT_SECS, TimeUnit.SECONDS)

    /**
     * Enables Logging of http requests in app logger on trace level
     */
    private val loggerInterceptor = HttpLoggingInterceptor { message -> logger.trace { message } }
        .apply {
            // Set logging level for HTTP requests to full request and response
            // Http requests are logged only on trace app logger level
            level = HttpLoggingInterceptor.Level.BODY

            //Do not show sensitive information
            redactHeader("Authorization")
            redactHeader("Cookie")
        }

    /**
     * Construct http client with custom user agent, connection pool and timeouts
     */
    override val client: OkHttpClient = OkHttpClient.Builder()
        // The whole call should not take longer than 20 seconds
        .callTimeout(TIMEOUT_SECS, TimeUnit.SECONDS)
        .cache(Cache(File(System.getProperty("java.io.tmpdir") , CACHE_BASE_DIR), MAX_CACHE_SIZE))
        .addNetworkInterceptor(UserAgentInterceptor())
        .addNetworkInterceptor(CacheInterceptor())
        .connectionPool(connectionPool)
        .addInterceptor(loggerInterceptor)
        .build()
}
