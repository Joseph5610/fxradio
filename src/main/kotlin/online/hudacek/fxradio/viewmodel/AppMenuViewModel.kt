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

import javafx.beans.property.BooleanProperty
import javafx.stage.StageStyle
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.events.data.AppNotification
import online.hudacek.fxradio.storage.ImageCache
import online.hudacek.fxradio.ui.formatted
import online.hudacek.fxradio.ui.modal.*
import online.hudacek.fxradio.ui.openUrl
import online.hudacek.fxradio.usecase.ClearCacheUseCase
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.macos.MacUtils
import online.hudacek.fxradio.utils.property
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class AppMenu(usePlatform: Boolean = MacUtils.isMac && property(Properties.PLATFORM_MENU_BAR, true)) {
    var usePlatform: Boolean by property(usePlatform)
}

class AppMenuViewModel : ItemViewModel<AppMenu>(AppMenu()) {

    private val appEvent: AppEvent by inject()

    private val clearCacheUseCase: ClearCacheUseCase by inject()

    val usePlatformProperty = bind(AppMenu::usePlatform) as BooleanProperty

    //Station menu links
    fun openStationInfo() = find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAddNewStation() = find<AddStationFragment>().openModal(stageStyle = StageStyle.UTILITY)

    //About menu links
    fun openAbout() = find<AboutFragment>().openModal(stageStyle = StageStyle.UTILITY, resizable = false)

    fun openAvailableServer() = find<ServersFragment>().openModal(stageStyle = StageStyle.UTILITY, resizable = false)

    //Help menu links
    fun openStats() = find<StatsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun clearCache() = if (ImageCache.totalSize < 1) {
        appEvent.appNotification.onNext(AppNotification(messages["cache.clear.empty"], FontAwesome.Glyph.CHECK))
    } else {
        confirm(messages["cache.clear.confirm"],
                messages["cache.clear.text"].formatted(ImageCache.totalSize), owner = primaryStage) {
            clearCacheUseCase.execute(Unit) success {
                appEvent.appNotification.onNext(
                        AppNotification(messages["cache.clear.ok"], FontAwesome.Glyph.CHECK))
            } fail {
                appEvent.appNotification.onNext(
                        AppNotification(messages["cache.clear.error"], FontAwesome.Glyph.WARNING))
            }
        }
    }

    fun openWebsite() = app.openUrl(FxRadio.appUrl)
}