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

package online.hudacek.fxradio.utils

import javafx.animation.PauseTransition
import javafx.event.EventHandler
import javafx.util.Duration
import mu.KotlinLogging
import online.hudacek.fxradio.macos.MacUtils
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.controlsfx.toGlyph

private val logger = KotlinLogging.logger {}

/**
 * Custom function for showing notification in NotificationPane.
 * Notification disappears after 5 seconds
 *
 * Example usage:
 * notificationPane[FontAwesome.Glyph.WARNING] = "Custom notification Text"
 */
internal operator fun NotificationPane.set(glyph: FontAwesome.Glyph, message: String) {
    if (isVisible) show(message, glyph.toGlyph())
    val delay = PauseTransition(Duration.seconds(5.0))
    delay.onFinished = EventHandler { hide() }
    delay.play()
}

internal fun NotificationPane.show(glyph: FontAwesome.Glyph,
                                   message: String,
                                   op: NotificationPane.() -> Unit = {}) {
    op(this)
    this[glyph] = message
}

/**
 * Show Native OS notification
 *
 */
fun notification(title: String, subtitle: String) {
    if (MacUtils.isMac) {
        MacUtils.notification(title, subtitle)
    } else {
        //not implemented error
        logger.debug { "Trying to shown notification on not implemented platform" }
    }
}