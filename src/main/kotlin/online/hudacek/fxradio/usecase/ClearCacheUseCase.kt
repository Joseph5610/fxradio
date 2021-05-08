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

package online.hudacek.fxradio.usecase

import online.hudacek.fxradio.events.data.AppNotification
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
