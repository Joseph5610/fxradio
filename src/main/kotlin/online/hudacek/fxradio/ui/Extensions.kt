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

package online.hudacek.fxradio.ui

import griffon.javafx.support.flagicons.FlagIcon
import javafx.animation.PauseTransition
import javafx.beans.property.ListProperty
import javafx.beans.property.Property
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.stage.Window
import javafx.util.Duration
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.api.model.Country
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.api.model.countryCode
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.view.player.TickerView
import org.controlsfx.control.NotificationPane
import org.controlsfx.control.textfield.CustomTextField
import org.controlsfx.control.textfield.TextFields
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.bindAutoCompletion
import tornadofx.controlsfx.notificationPane
import tornadofx.controlsfx.toGlyph
import java.net.URLEncoder
import java.text.MessageFormat

/**
 * This is to overcome a bug that sometimes
 * scene is not available when requesting focus
 */
internal fun Node.requestFocusOnSceneAvailable() = if (scene == null) {
    val listener = object : ChangeListener<Scene> {
        override fun changed(observable: ObservableValue<out Scene>?, oldValue: Scene?, newValue: Scene?) {
            if (newValue != null) {
                sceneProperty().removeListener(this)
                requestFocus()
            }
        }
    }
    sceneProperty().addListener(listener)
} else {
    requestFocus()
}

internal fun EventTarget.smallLabel(text: String = "", op: Label.() -> Unit = {}) = label(text) {
    addClass(Styles.boldText)
    addClass(Styles.grayLabel)
    op(this)
}

internal fun FontAwesome.Glyph.make(
        size: Double = 35.0,
        useStyle: Boolean = true,
        color: Color? = null) = toGlyph {
    size(size)
    if (color != null) {
        style {
            textFill = color
        }
    }
    if (useStyle) {
        style {
            padding = box(10.px, 5.px)
        }
    }
}

internal fun EventTarget.searchField(promptText: String, property: ObservableValue<String>, op: (CustomTextField.() -> Unit) = {}) = searchField {
    this.promptText = promptText
    bind(property)
    op(this)
}

internal fun EventTarget.searchField(op: (CustomTextField.() -> Unit) = {}): CustomTextField =
        opcr(this, TextFields.createClearableTextField() as CustomTextField, op)

internal fun tickerView(op: TickerView.() -> Unit = {}): TickerView {
    return TickerView().apply {
        op(this)
    }
}

/**
 * Copy Menu
 */
internal fun EventTarget.copyMenu(clipboard: Clipboard,
                                  name: String,
                                  value: String = "") = contextmenu {
    item(name) {
        action {
            clipboard.update(value)
        }
    }
}

internal fun EventTarget.autoUpdatingCopyMenu(clipboard: Clipboard,
                                              menuItemName: String,
                                              valueToCopy: StringProperty) = contextmenu {
    item(menuItemName) {
        action {
            if (valueToCopy.value != null) {
                clipboard.update(valueToCopy.value)
            }
        }

        valueToCopy.onChange {
            action {
                clipboard.update(it!!)
            }
        }
    }
}

internal fun Clipboard.update(newValue: String) = setContent {
    putString(newValue)
}

internal fun Window.setOnSpacePressed(action: () -> Unit) {
    addEventHandler(KeyEvent.KEY_PRESSED) {
        if (it.code == KeyCode.SPACE) {
            action()
        }
    }
}

/**
 * Open URL in user's internet browser
 */
internal fun App.openUrl(url: String, query: String = "") {
    val queryEncoded = URLEncoder.encode(query, "UTF-8")
    hostServices.showDocument(url + queryEncoded)
}

internal fun <T : Node> T.showWhen(expr: () -> ObservableValue<Boolean>): T =
        visibleWhen(expr()).apply {
            managedWhen(expr())
        }

/**
 * Menu helpers
 */
internal fun menu(name: String, op: Menu.() -> Unit = {}) = Menu(name).apply {
    op(this)
}

internal fun MenuItem.disableWhenInvalidStation(station: Property<Station>) {
    disableWhen(station.booleanBinding {
        it == null || !it.isValid()
    })
}

/**
 * Notification UI helpers
 */
internal fun EventTarget.stylableNotificationPane(op: (NotificationPane.() -> Unit) = {}) = notificationPane(showFromTop = true) {
    //Show dark notifications
    if (FxRadio.isAppInDarkMode) {
        styleClass.add(NotificationPane.STYLE_CLASS_DARK)
    }
    op(this)
}

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

internal fun String.formatted(replaceWith: Any) = MessageFormat.format(this, replaceWith)

internal fun EventTarget.field(message: String, prompt: String,
                               property: Property<String>,
                               isRequired: Boolean = false,
                               autoCompleteProperty: ListProperty<String>? = null,
                               op: (TextField) -> Unit = {}) =
        add(field(message) {
            textfield(property) {
                if (autoCompleteProperty != null) bindAutoCompletion(autoCompleteProperty)
                if (isRequired) required()
                promptText = prompt
                op(this)
            }
        })

val Country.flagIcon: FlagIcon?
    get() {
        return try {
            countryCode?.let { FlagIcon(it) }
        } catch (e: IllegalArgumentException) {
            null
        }
    }