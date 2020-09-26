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

package online.hudacek.fxradio.viewmodel

import io.reactivex.disposables.Disposable
import javafx.stage.StageStyle
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.fragments.*
import online.hudacek.fxradio.macos.MacUtils
import online.hudacek.fxradio.storage.ImageCache
import online.hudacek.fxradio.utils.applySchedulers
import online.hudacek.fxradio.utils.openUrl
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

//WIP
class MenuModel

class MenuViewModel : ItemViewModel<MenuModel>() {

    private val logger = KotlinLogging.logger {}

    private val playerViewModel: PlayerViewModel by inject()

    val useNative: Boolean
        get() = MacUtils.isMac && app.config.boolean(Config.Keys.useNativeMenuBar, true)

    fun openStats() = find<StatsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openStationInfo() = find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAttributions() = find<AttributionsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAbout() = find<AboutFragment>().openModal(stageStyle = StageStyle.UTILITY, resizable = false)

    fun openAddNewStation() = find<AddStationFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun clearCache() = runAsync(daemon = true) {
        ImageCache.clear()
    } success {
        fire(NotificationEvent(messages["cache.clear.ok"], FontAwesome.Glyph.CHECK))
    } fail {
        fire(NotificationEvent(messages["cache.clear.error"]))
        logger.error(it) { "Exception when clearing cache" }
    }

    fun handleVote(): Disposable = StationsApi.service
            .vote(playerViewModel.stationProperty.value.stationuuid)
            .compose(applySchedulers())
            .subscribe({
                fire(NotificationEvent(messages["vote.ok"], FontAwesome.Glyph.CHECK))
            }, {
                fire(NotificationEvent(messages["vote.error"]))
            })

    fun openWebsite() = app.openUrl(FxRadio.appUrl)
}