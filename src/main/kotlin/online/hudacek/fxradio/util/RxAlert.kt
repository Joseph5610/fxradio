/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2023  hudacek.online
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

import io.reactivex.rxjava3.core.Maybe
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.layout.Region
import online.hudacek.fxradio.ui.style.Styles
import tornadofx.FX
import tornadofx.addClass

object RxAlert {

    /**
     * Shows confirmation alert dialog
     */
    fun confirm(header: String, content: String) = rxAlert(type = AlertType.CONFIRMATION, header, content)

    /**
     * Shows warning alert dialog
     */
    fun warning(header: String, content: String) = rxAlert(type = AlertType.WARNING, header, content)

    /**
     * Constructs reactive alert modal dialog
     */
    private fun rxAlert(
        type: AlertType = AlertType.INFORMATION,
        header: String,
        content: String,
        title: String? = null
    ): Maybe<ButtonType> {
        val alert = Alert(type, content)
        title?.let { alert.title = it }
        alert.headerText = header
        alert.initOwner(FX.primaryStage)
        alert.dialogPane.minHeight = Region.USE_PREF_SIZE

        val okButton: Button? =
            alert.dialogPane.lookupButton(alert.buttonTypes.firstOrNull { it == ButtonType.OK }) as Button?
        okButton?.addClass(Styles.primaryButton)
        return alert.toMaybe()
            .defaultIfEmpty(ButtonType.CANCEL)
            .filter { it == ButtonType.OK }
    }
}