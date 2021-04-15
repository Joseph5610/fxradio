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

package online.hudacek.fxradio.media

import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.events.data.AppNotification
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.find

private val logger = KotlinLogging.logger {}

class StreamUnavailableException(message: String, cause: Throwable?) : Exception(message, cause) {

    constructor(message: String) : this(message, null)

    init {
        Platform.runLater {
            find<PlayerViewModel>().stateProperty.value = PlayerState.Error
            find<AppEvent>().appNotification.onNext(AppNotification(localizedMessage, FontAwesome.Glyph.WARNING))
            logger.error(this) { "Stream can't be played" }
        }
    }
}