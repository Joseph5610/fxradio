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
import io.reactivex.Maybe
import io.reactivex.MaybeTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.style.StylesDark
import online.hudacek.fxradio.util.macos.MacUtils
import tornadofx.FX
import tornadofx.importStylesheet

/**
 * Perform async calls on correct thread
 */
internal fun <T> applySchedulersSingle(): SingleTransformer<T, T> = SingleTransformer {
    it.subscribeOn(Schedulers.io())
        .observeOnFx()
}

internal fun <T> applySchedulersMaybe(): MaybeTransformer<T, T> = MaybeTransformer {
    it.subscribeOn(Schedulers.io())
        .observeOnFx()
}

internal fun <T> applySchedulers(): ObservableTransformer<T, T> = ObservableTransformer {
    it.subscribeOn(Schedulers.io())
        .observeOnFx()
}

internal fun <T> maybeOfNullable(value: T?): Maybe<T> {
    return if (value == null) Maybe.empty() else Maybe.just(value)
}

internal fun reloadStylesheets(isDarkModeProperty: Boolean) {
    val scene = FX.primaryStage.scene
    FX.stylesheets.clear()
    scene.stylesheets.clear()

    if (isDarkModeProperty) {
        importStylesheet(StylesDark::class)
    } else {
        importStylesheet(Styles::class)
    }
    FX.applyStylesheetsTo(scene)
}

internal fun keyCombination(keyCode: KeyCode) =
    KeyCodeCombination(keyCode, if (MacUtils.isMac) KeyCombination.META_DOWN else KeyCombination.CONTROL_DOWN)


