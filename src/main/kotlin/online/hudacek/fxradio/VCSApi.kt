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

package online.hudacek.fxradio

import io.reactivex.Observable
import javafx.application.Platform
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import mu.KotlinLogging
import online.hudacek.fxradio.extension.openUrl
import online.hudacek.fxradio.model.rest.vcs.VCSResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import tornadofx.*
import kotlin.system.exitProcess

/**
 * Simple version check api
 */
interface VCSApi {

    @GET("vcs/currentVersion.json")
    fun currentVersion(@Query("version") version: String): Observable<VCSResponse>

    companion object : Component() {
        private val logger = KotlinLogging.logger {}

        val client: VCSApi
            get() = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(FxRadio.appUrl)
                    .build()
                    .create(VCSApi::class.java)

        fun checkCurrentVersion() {
            logger.debug { "Started VCS Check" }
            client
                    .currentVersion(FxRadio.version)
                    .subscribe(
                            {
                                FxRadio.version.toDoubleOrNull()?.let { appVersion ->
                                    if (it.currentVersion > appVersion) {
                                        logger.info { "There is a new version ${it.currentVersion}" }
                                        val dialogButtons = arrayOf(
                                                ButtonType(messages["vcs.download"], ButtonBar.ButtonData.YES),
                                                ButtonType(if (it.required) {
                                                    messages["vcs.close.app"]
                                                } else {
                                                    messages["vcs.close"]
                                                }, ButtonBar.ButtonData.NO)
                                        )
                                        information(
                                                header = it.languages[0].message,
                                                content = it.languages[0].description, buttons = *dialogButtons
                                        ) { buttonType ->
                                            if (buttonType.buttonData == ButtonBar.ButtonData.NO) {
                                                if (it.required) {
                                                    Platform.exit()
                                                    exitProcess(0)
                                                }
                                            } else if (buttonType.buttonData == ButtonBar.ButtonData.YES) {
                                                app.openUrl(it.downloadUrl)
                                                this.showAndWait()
                                            } else {
                                                this.showAndWait()
                                            }
                                        }
                                    }
                                }
                            }, { logger.error(it) { "There was an error connecting to VCS server!" } }
                    )
        }
    }

}