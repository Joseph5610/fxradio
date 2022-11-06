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

package online.hudacek.fxradio.viewmodel

import io.reactivex.disposables.Disposable
import javafx.beans.property.BooleanProperty
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.ui.openUrl
import online.hudacek.fxradio.usecase.CacheClearUseCase
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.macos.MacUtils
import online.hudacek.fxradio.util.value
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.get
import tornadofx.property

class AppMenu(usePlatform: Boolean = MacUtils.isMac && Properties.UseNativeMenuBar.value(true)) {
    var usePlatform: Boolean by property(usePlatform)
}

class AppMenuViewModel : BaseViewModel<AppMenu>(AppMenu()) {

    private val cacheClearUseCase: CacheClearUseCase by inject()

    val usePlatformProperty = bind(AppMenu::usePlatform) as BooleanProperty

    fun clearCache(): Disposable = cacheClearUseCase.execute(Unit)
        .subscribe({
            appEvent.appNotification.onNext(
                AppNotification(messages["cache.clear.ok"], FontAwesome.Glyph.CHECK)
            )
        }, {
            appEvent.appNotification.onNext(
                AppNotification(messages["cache.clear.error"], FontAwesome.Glyph.WARNING)
            )
        })

    fun openWebsite() = app.openUrl(FxRadio.appUrl)
}
