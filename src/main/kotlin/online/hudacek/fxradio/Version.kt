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

import com.vdurmont.semver4j.Semver
import com.vdurmont.semver4j.SemverException
import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.fxradio.api.VCSApi
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.extension.applySchedulers
import online.hudacek.fxradio.extension.openUrl
import org.controlsfx.control.action.Action
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

data class Version(val version: String) : Semver(version, SemverType.LOOSE)

/**
 * Logic for version checks with remote API
 */
//TODO rewrite
object VersionCheck : Component() {

    private val logger = KotlinLogging.logger {}

    fun perform() {
        VCSApi.service.currentVersion()
                .compose(applySchedulers())
                .subscribe({ vcs ->
                    try {
                        val latestVersion = Semver(vcs.currentVersion, Semver.SemverType.LOOSE)
                        if (FxRadio.version.isEqualTo(latestVersion)) {
                            fire(NotificationEvent(messages["vcs.uptodate"], FontAwesome.Glyph.CHECK))
                        } else if (latestVersion.isGreaterThan(FxRadio.version)) {
                            logger.info { "There is a new version ${vcs.currentVersion}" }
                            if (vcs.required) {
                                confirm(vcs.languages[0].message, vcs.languages[0].description) {
                                    Platform.exit()
                                }
                            } else {
                                fire(NotificationEvent(vcs.languages[0].message, op = {
                                    actions.setAll(Action(messages["vcs.download"]) {
                                        app.openUrl(vcs.downloadUrl)
                                    })
                                }))
                            }
                        }
                    } catch (e: SemverException) {
                        logger.error(e) { "Can't parse version string, acting as no update available" }
                        fire(NotificationEvent(messages["vcs.uptodate"], FontAwesome.Glyph.CHECK))
                    }
                }, {
                    logger.error(it) { "VCS check problem!" }
                })
    }
}