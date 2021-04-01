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

package online.hudacek.fxradio.ui.viewmodel

import javafx.beans.property.BooleanProperty
import javafx.stage.StageStyle
import mu.KotlinLogging
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.events.data.AppNotification
import online.hudacek.fxradio.storage.ImageCache
import online.hudacek.fxradio.ui.modal.*
import online.hudacek.fxradio.ui.openUrl
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

private val logger = KotlinLogging.logger {}

class AppMenu(usePlatform: Boolean = true) {
    var usePlatform: Boolean by property(usePlatform)
}

class AppMenuViewModel : ItemViewModel<AppMenu>(AppMenu()) {
    private val appEvent: AppEvent by inject()

    val usePlatformProperty = bind(AppMenu::usePlatform) as BooleanProperty

    //Station menu links
    fun openStationInfo() = find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAddNewStation() = find<AddStationFragment>().openModal(stageStyle = StageStyle.UTILITY)

    //About menu links
    fun openAbout() = find<AboutFragment>().openModal(stageStyle = StageStyle.UTILITY, resizable = false)

    fun openAvailableServer() = find<AvailableServersFragment>().openModal(stageStyle = StageStyle.UTILITY, resizable = false)

    //Help menu links
    fun openStats() = find<StatsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun clearCache() = runAsync(daemon = true) {
        ImageCache.clear()
    } success {
        appEvent.appNotification.onNext(
                AppNotification(messages["cache.clear.ok"],
                        FontAwesome.Glyph.CHECK))
    } fail {
        appEvent.appNotification.onNext(
                AppNotification(messages["cache.clear.error"],
                        FontAwesome.Glyph.WARNING))
        logger.error(it) { "Exception when clearing cache" }
    }

    fun openWebsite() = app.openUrl(FxRadio.appUrl)
}