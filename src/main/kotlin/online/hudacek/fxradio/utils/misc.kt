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

import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.input.Clipboard
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Window
import mu.KotlinLogging
import online.hudacek.fxradio.macos.MacUtils
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.views.player.TickerView
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.glyph
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLEncoder

private val logger = KotlinLogging.logger {}


/*
 * Helper extension functions for UI
 */
internal fun EventTarget.smallLabel(text: String = "", op: Label.() -> Unit = {}) = label(text) {
    paddingLeft = 10.0
    addClass(Styles.boldText)
    addClass(Styles.grayLabel)
    op.invoke(this)
}

internal fun EventTarget.glyph(glyph: FontAwesome.Glyph, size: Double = 35.0, useStyle: Boolean = true) = glyph("FontAwesome", glyph) {
    size(size)
    if (useStyle) {
        style {
            padding = box(10.px, 5.px)
        }
    }
}

internal fun tickerView(op: TickerView.() -> Unit = {}): TickerView {
    return TickerView().apply {
        op.invoke(this)
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

internal fun Window.setOnSpacePressed(action: () -> Unit) {
    addEventHandler(KeyEvent.KEY_PRESSED) {
        if (it.code == KeyCode.SPACE) {
            action.invoke()
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

internal fun <T : Node> T.showWhen(expr: () -> ObservableValue<Boolean>): T =
        visibleWhen(expr()).apply {
            managedWhen(expr())
        }

/**
 * Perform async calls on correct thread
 */
internal fun <T> applySchedulers(): SingleTransformer<T, T>? =
        SingleTransformer {
            it.subscribeOn(Schedulers.io())
                    .observeOnFx()
        }

//Command line utilities
internal fun command(command: String) = Runtime.getRuntime().exec(command)

internal val Process.result: String
    get() {
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.forEachLine {
            sb.append(it)
        }
        return sb.toString()
    }

internal val isSystemDarkMode: Boolean
    get() {
        return if (MacUtils.isMac) MacUtils.isInDarkMode()
        else {
            logger.debug { "isSystemDarkMode: Unsupported OS" }
            false
        }
    }