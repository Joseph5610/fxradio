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

package online.hudacek.fxradio.controllers

import com.vdurmont.semver4j.Semver
import com.vdurmont.semver4j.SemverException
import io.reactivex.disposables.Disposable
import javafx.application.Platform
import javafx.stage.Stage
import javafx.stage.StageStyle
import mu.KotlinLogging
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.VCSApi
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.extension.applySchedulers
import online.hudacek.fxradio.extension.openUrl
import online.hudacek.fxradio.fragments.*
import online.hudacek.fxradio.media.MediaPlayerWrapper
import online.hudacek.fxradio.storage.ImageCache
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.views.menubar.MenuBarView
import org.controlsfx.control.action.Action
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class MenuBarController : Controller() {

    private val logger = KotlinLogging.logger {}

    private val menuBarView: MenuBarView by inject()

    private val mediaPlayerWrapper: MediaPlayerWrapper by inject()
    private val playerViewModel: PlayerViewModel by inject()

    fun openStats() = find<StatsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openStationInfo() = find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAttributions() = find<AttributionsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAbout() = find<AboutFragment>().openModal(stageStyle = StageStyle.UTILITY, resizable = false)

    fun clearCache() = runAsync(daemon = true) {
        ImageCache.clear()
    } success {
        fire(NotificationEvent(messages["cache.clear.ok"], FontAwesome.Glyph.CHECK))
    } fail {
        fire(NotificationEvent(messages["cache.clear.error"]))
        logger.error(it) { "Exception when clearing cache" }
    }

    fun closeApp(currentStage: Stage?) {
        currentStage?.close()
        mediaPlayerWrapper.release()
    }

    fun openAddNewStation() = find<AddStationFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun voteForStation(): Disposable = StationsApi.service
            .vote(playerViewModel.stationProperty.value.stationuuid)
            .compose(applySchedulers())
            .subscribe({
                menuBarView.showVoteResult(it.ok)
            }, {
                menuBarView.showVoteResult(false)
            })

    fun openWebsite() = app.openUrl(FxRadio.appUrl)

    fun checkForUpdate() {
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