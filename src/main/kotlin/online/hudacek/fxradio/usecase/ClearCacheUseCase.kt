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

package online.hudacek.fxradio.usecase

import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.storage.ImageCache
import online.hudacek.fxradio.ui.formatted
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.confirm
import tornadofx.fail
import tornadofx.get
import tornadofx.success

/**
 * Clears FxRadio image cache directory
 */
class ClearCacheUseCase : BaseUseCase<Unit, Unit>() {

    override fun execute(input: Unit) =
            if (ImageCache.totalSize < 1) {
                appEvent.appNotification.onNext(AppNotification(messages["cache.clear.empty"], FontAwesome.Glyph.CHECK))
            } else {
                confirm(messages["cache.clear.confirm"],
                        messages["cache.clear.text"].formatted(ImageCache.totalSize), owner = primaryStage) {
                    runAsync(daemon = true) {
                        ImageCache.clear()
                    } success {
                        appEvent.appNotification.onNext(
                                AppNotification(messages["cache.clear.ok"], FontAwesome.Glyph.CHECK))
                    } fail {
                        appEvent.appNotification.onNext(
                                AppNotification(messages["cache.clear.error"], FontAwesome.Glyph.WARNING))
                    }
                }
            }
}
