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

package online.hudacek.fxradio.unit

import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.api.ApiClient
import online.hudacek.fxradio.api.BasicHttpClient
import online.hudacek.fxradio.api.StationsApi
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ApiTest {

    @Test
    fun apiTest() {
        val hostname = "https://de1.api.radio-browser.info"
        val client = ApiClient(hostname)
        client.create(StationsApi::class)
                .getTopStations()
                .subscribe { stations ->
                    Assertions.assertEquals(50, stations.size)
                    stations.forEach {
                        //top 50 stations should not have empty URL and have name
                        Assertions.assertTrue(it.name.isNotEmpty())
                        Assertions.assertTrue(it.url_resolved != null)
                    }
                }.dispose()
    }


    @Test
    fun basicHttpTest() {
        val client = BasicHttpClient(FxRadio.appUrl)
        var performed = false

        client.call(
                success = {
                    //Check response
                    Assertions.assertTrue(code() == 200)
                    println(this.body()?.string())
                    performed = true
                },
                fail = {
                    performed = true
                    Assertions.fail(this)
                }
        )

        //Wait for finish
        await().until { performed }
    }
}