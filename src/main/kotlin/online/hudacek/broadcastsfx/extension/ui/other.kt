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

package online.hudacek.broadcastsfx.extension.ui

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory
import javafx.event.EventTarget
import javafx.scene.control.ContextMenu
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.glyph
import java.net.URLEncoder

/*
 * Helper extension functions for UI
 */
internal fun EventTarget.smallLabel(text: String) = label(text) {
    paddingLeft = 10.0
    addClass(Styles.boldText)
    addClass(Styles.grayLabel)
}

internal fun EventTarget.smallIcon(url: String, op: ImageView.() -> Unit = {}) =
        imageview(url, op = op).apply {
            fitWidth = 14.0
            fitHeight = 14.0
        }

internal fun EventTarget.glyph(glyph: FontAwesome.Glyph) = glyph("FontAwesome", glyph) {
    size(35.0)
    style {
        padding = box(10.px, 5.px)
    }
}

internal fun EventTarget.copyMenu(clipboard: Clipboard,
                                  name: String,
                                  value: String = "", op: ContextMenu.() -> Unit = {}) = contextmenu {
    item(name) {
        action {
            clipboard.setContent {
                putString(value)
            }
        }
    }
    op(this)
}

internal fun Clipboard.update(newValue: String) = setContent {
    putString(newValue)
}

internal fun EventTarget.setOnSpacePressed(action: () -> Unit) {
    keyboard {
        addEventHandler(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.SPACE) {
                action.invoke()
            }
        }
    }
}

/**
 * Open URL in user's internet browser
 */
internal fun App.openUrl(url: String, query: String = "") {
    val queryEncoded = URLEncoder.encode(query, "UTF-8")
    val hostServices = HostServicesFactory.getInstance(this)
    hostServices.showDocument(url + queryEncoded)
}