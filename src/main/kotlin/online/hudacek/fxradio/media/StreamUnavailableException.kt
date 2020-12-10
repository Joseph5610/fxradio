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
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import online.hudacek.fxradio.ui.viewmodel.PlayingStatus
import tornadofx.FX
import tornadofx.find

class StreamUnavailableException(message: String, cause: Throwable?) : Exception(message, cause) {

    constructor(message: String) : this(message, null)

    private val logger = KotlinLogging.logger {}

    init {
        Platform.runLater {
            find<PlayerViewModel>().playingStatusProperty.value = PlayingStatus.Error
            FX.eventbus.fire(NotificationEvent(localizedMessage))
            logger.error(this) { "Stream can't be played" }
        }
    }
}