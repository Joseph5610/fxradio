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

import io.reactivex.rxjava3.core.Observable
import javafx.animation.PauseTransition
import javafx.beans.property.ListProperty
import javafx.beans.property.Property
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.input.Clipboard
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Window
import javafx.util.Duration
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.ui.menu.platformContextMenu
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
import tornadofx.controlsfx.bindAutoCompletion
import tornadofx.controlsfx.toGlyph
import tornadofx.field
import tornadofx.item
import tornadofx.label
import tornadofx.managedWhen
import tornadofx.onChange
import tornadofx.opcr
import tornadofx.putString
import tornadofx.required
import tornadofx.textfield
import tornadofx.visibleWhen
import java.net.URLEncoder
import java.text.MessageFormat

private const val NOTIFICATION_TIME_ON_SCREEN = 3.0

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

internal fun EventTarget.autoUpdatingCopyMenu(
    clipboard: Clipboard,
    menuItemName: String,
    valueToCopy: StringProperty
) = platformContextMenu {
    item(menuItemName) {
        action {
            if (valueToCopy.value != null) {
                clipboard.putString(valueToCopy.value)
            }
        }

        valueToCopy.onChange {
            action {
                clipboard.putString(it!!)
            }
        }
    }
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
 * Custom function for showing notification in NotificationPane.
 * Notification disappears after [NOTIFICATION_TIME_ON_SCREEN]
 *
 * Example usage:
 * notificationPane[FontAwesome.Glyph.WARNING] = "Custom notification Text"
 */
internal operator fun NotificationPane.set(glyph: FontAwesome.Glyph, message: String) {
    if (isVisible) show(message, glyph.toGlyph())
    val delay = PauseTransition(Duration.seconds(NOTIFICATION_TIME_ON_SCREEN))
    delay.setOnFinished { hide() }
    delay.play()
}

internal fun String.msgFormat(replaceWith: Any) = MessageFormat.format(this, replaceWith)

internal fun EventTarget.field(
    message: String, prompt: String,
    property: Property<String>,
    isRequired: Boolean = false,
    autoCompleteProperty: ListProperty<String>? = null,
    op: (TextField) -> Unit = {}
) = add(field(message) {
    textfield(property) {
        if (autoCompleteProperty != null) bindAutoCompletion(autoCompleteProperty)
        if (isRequired) required()
        promptText = prompt
        op(this)
    }
})

internal fun EventTarget.stationView(station: Station, size: Double, op: StationImageView.() -> Unit = {}) =
    opcr(this, StationImageView(Observable.just(station), size), op)

internal fun EventTarget.stationView(
    stationObservable: Observable<Station>,
    size: Double,
    op: StationImageView.() -> Unit = {}
) = opcr(this, StationImageView(stationObservable, size), op)

internal val Country.flagIcon: Image?
    get() = FlagIcon(iso3166).get()