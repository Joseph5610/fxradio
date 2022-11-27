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

package online.hudacek.fxradio.ui.util

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
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Window
import javafx.util.Duration
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.view.StationImageView
import org.controlsfx.control.NotificationPane
import org.controlsfx.control.textfield.CustomTextField
import org.controlsfx.control.textfield.TextFields
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.App
import tornadofx.action
import tornadofx.add
import tornadofx.addClass
import tornadofx.bind
import tornadofx.contextmenu
import tornadofx.controlsfx.bindAutoCompletion
import tornadofx.controlsfx.notificationPane
import tornadofx.controlsfx.toGlyph
import tornadofx.field
import tornadofx.item
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.onChange
import tornadofx.opcr
import tornadofx.required
import tornadofx.setContent
import tornadofx.textfield
import tornadofx.visibleWhen
import java.net.URLEncoder
import java.text.MessageFormat

private const val NOTIFICATION_TIME_ON_SCREEN = 5.0

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
    size: Double,
    isPrimary: Boolean = true,
    op: Glyph.() -> Unit = {}
) = toGlyph {
    size(size)
    if (isPrimary) {
        addClass(Styles.glyphIconPrimary)
    } else {
        addClass(Styles.glyphIcon)
    }
    op(this)
}

internal fun EventTarget.searchField(
    promptText: String,
    property: ObservableValue<String>,
    op: (CustomTextField.() -> Unit) = {}
) = searchField {
    this.promptText = promptText
    bind(property)
    op(this)
}

internal fun EventTarget.searchField(op: (CustomTextField.() -> Unit) = {}): CustomTextField =
    opcr(this, TextFields.createClearableTextField() as CustomTextField, op)

/**
 * Copy Context Menu
 */
internal fun EventTarget.copyMenu(
    clipboard: Clipboard,
    name: String,
    value: String = ""
) = contextmenu {
    item(name) {
        action {
            clipboard.update(value)
        }
    }
}

internal fun EventTarget.autoUpdatingCopyMenu(
    clipboard: Clipboard,
    menuItemName: String,
    valueToCopy: StringProperty
) = contextmenu {
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
 * Notification UI helpers
 */
internal fun EventTarget.customNotificationPane(op: (NotificationPane.() -> Unit) = {}) =
    notificationPane(showFromTop = true) {
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
    val delay = PauseTransition(Duration.seconds(NOTIFICATION_TIME_ON_SCREEN))
    delay.onFinished = EventHandler { hide() }
    delay.play()
}

internal fun String.formatted(replaceWith: Any) = MessageFormat.format(this, replaceWith)

internal fun EventTarget.field(
    message: String, prompt: String,
    property: Property<String>,
    isRequired: Boolean = false,
    autoCompleteProperty: ListProperty<String>? = null,
    op: (TextField) -> Unit = {}
) =
    add(field(message) {
        textfield(property) {
            if (autoCompleteProperty != null) bindAutoCompletion(autoCompleteProperty)
            if (isRequired) required()
            promptText = prompt
            op(this)
        }
    })

fun EventTarget.stationView(station: Station, op: ImageView.() -> Unit = {}) =
    opcr(this, StationImageView(station), op)

fun EventTarget.stationView(stationProperty: Property<Station>, op: ImageView.() -> Unit = {}) =
    opcr(this, StationImageView(stationProperty), op)

internal val Country.flagIcon: FlagIcon?
    get() = runCatching { FlagIcon(iso3166) }.getOrNull()