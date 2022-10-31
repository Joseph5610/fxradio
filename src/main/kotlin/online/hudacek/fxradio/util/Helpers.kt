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

package online.hudacek.fxradio.util

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.Window
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.style.StylesDark
import online.hudacek.fxradio.util.macos.MacUtils
import tornadofx.*
import tornadofx.FX.Companion.messages
import java.util.*

/**
 * Perform async calls on correct thread
 */
internal fun <T> applySchedulersSingle(): SingleTransformer<T, T> = SingleTransformer {
    it.subscribeOn(Schedulers.io())
        .observeOnFx()
}

internal fun <T> applySchedulers(): ObservableTransformer<T, T> = ObservableTransformer {
    it.subscribeOn(Schedulers.io())
        .observeOnFx()
}

internal fun vlcAlert() = alert(
    Alert.AlertType.ERROR,
    messages["player.vlc.missing"], messages["player.vlc.missing.description"]
)

internal fun confirmDialog(
    header: String, content: String = "",
    confirmButton: ButtonType = ButtonType.OK,
    cancelButton: ButtonType = ButtonType.CANCEL,
    owner: Window? = null, title: String? = null
): Alert {
    val alert = Alert(Alert.AlertType.CONFIRMATION, content, confirmButton, cancelButton)
    title?.let { alert.title = it }
    alert.headerText = header
    owner?.also { alert.initOwner(it) }
    return alert
}

internal fun reloadStylesheets(isDarkModeProperty: Boolean) {
    FX.stylesheets.clear()
    if (isDarkModeProperty) {
        importStylesheet(StylesDark::class)
    } else {
        importStylesheet(Styles::class)
    }
    FX.applyStylesheetsTo(FX.primaryStage.scene)
}

internal fun keyCombination(keyCode: KeyCode) =
    KeyCodeCombination(keyCode, if (MacUtils.isMac) KeyCombination.META_DOWN else KeyCombination.CONTROL_DOWN)

internal fun getCountryNameFromISO(iso3166: String?): String? {
    val countryCode: String? = Locale.getISOCountries().firstOrNull { it == iso3166 }
    return if (countryCode != null) {
        Locale("", countryCode).displayName
    } else {
        null
    }
}

