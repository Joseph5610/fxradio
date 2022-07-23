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
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.style.StylesDark
import tornadofx.FX
import tornadofx.alert
import tornadofx.importStylesheet
import tornadofx.removeStylesheet

/**
 * Perform async calls on correct thread
 */
internal fun <T> applySchedulers(): SingleTransformer<T, T> = SingleTransformer {
    it.subscribeOn(Schedulers.io())
            .observeOnFx()
}

internal fun <T> applySchedulersObservable(): ObservableTransformer<T, T> = ObservableTransformer {
    it.subscribeOn(Schedulers.io())
            .observeOnFx()
}

internal fun vlcAlert() = alert(Alert.AlertType.ERROR,
        "VLC player was not found!",
        "The app will try to use different player. For the best listening experience, we recommend that you install VLC player on your system!")
