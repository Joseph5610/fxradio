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
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import online.hudacek.fxradio.apiclient.http.interceptor.UserAgentInterceptor
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

/**
 * Defines Connection timeout for the duration og w in seconds
 */
private const val timeoutInSeconds: Long = 20

/**
 * Base OkHttpClient implementation
 */
class BasicClientProvider : HttpClientProvider() {

    /**
     * Defines the limits for the active connections
     */
    private val connectionPool = ConnectionPool(5, timeoutInSeconds, TimeUnit.SECONDS)

    /**
     * Enables Logging of http requests in app logger on trace level
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

}