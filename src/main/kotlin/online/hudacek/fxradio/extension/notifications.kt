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

package online.hudacek.fxradio.extension

import airsquared.JMacNotification.NSUserNotification
import javafx.animation.PauseTransition
import javafx.event.EventHandler
import javafx.util.Duration
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.tools.Platform
import tornadofx.controlsfx.glyph

/**
 * Custom function for showing notification in NotificationPane.
 * Notification disappears after 5 seconds
 *
 * Example usage:
 * notificationPane[FontAwesome.Glyph.WARNING] = "Custom notification Text"
 */
internal operator fun NotificationPane.set(glyph: FontAwesome.Glyph, message: String) {
    if (isVisible) show(message, glyph("FontAwesome", glyph))
    val delay = PauseTransition(Duration.seconds(5.0))
    delay.onFinished = EventHandler { hide() }
    delay.play()
}

/**
 * Show Native OS notification
 *
 */

fun notification(title: String, subtitle: String) {
    if (Platform.getCurrent() == Platform.OSX) {
        macNotification(title, subtitle)
    } else {
        //not implemented
    }
}

//MacOS native notification
private fun macNotification(title: String, subtitle: String) =
        NSUserNotification().apply {
            this.title = title
            this.informativeText = subtitle
            show()
        }