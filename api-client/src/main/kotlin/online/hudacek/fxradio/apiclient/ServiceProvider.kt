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

package online.hudacek.fxradio.apiclient

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import online.hudacek.fxradio.apiclient.http.provider.AbstractClientProvider
import online.hudacek.fxradio.apiclient.http.provider.CachingClientProvider
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * Provides Retrofit instance for [baseUrl]
 */
class ServiceProvider(
    private val baseUrl: String,
    private val clientProvider: AbstractClientProvider = CachingClientProvider(),
) {

    /**
     * Retrofit client instance for [baseUrl] with custom [clientProvider]
     */
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper))
            .baseUrl(baseUrl)
            .client(clientProvider.client)
            .build()

    /**
     * Constructs Retrofit service class of type [T]
     */
    inline fun <reified T : ApiDefinition> create(): T = retrofit.create(T::class.java)

    fun close() = clientProvider.close()

    companion object {

        /**
         * instance of kotlin jackson object mapper
         */
        private val jacksonObjectMapper = jacksonObjectMapper()
    }
}
