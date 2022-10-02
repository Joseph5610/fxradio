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

package online.hudacek.fxradio.ui.menu

import javafx.beans.property.Property
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCodeCombination
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import tornadofx.bind
import tornadofx.booleanBinding
import tornadofx.disableWhen

/**
 * Menu helpers
 */
internal fun menu(name: String, op: Menu.() -> Unit = {}) = Menu(name).apply {
    op(this)
}

internal fun item(name: String, keyCode: KeyCodeCombination? = null,
                  op: MenuItem.() -> Unit = {}) = MenuItem(name).apply {
    if (keyCode != null) {
        accelerator = keyCode
    }
    op(this)
}

internal fun checkMenuItem(name: String, bindProperty: Property<Boolean>? = null,
                           keyCode: KeyCodeCombination? = null,
                           op: CheckMenuItem.() -> Unit = {}) = CheckMenuItem(name).apply {
    if (keyCode != null) {
        accelerator = keyCode
    }
    if (bindProperty != null) {
        bind(bindProperty)
    }
    op(this)
}

internal fun separator() = SeparatorMenuItem()

internal fun MenuItem.disableWhenInvalidStation(station: Property<Station>) {
    disableWhen(station.booleanBinding {
        it == null || !it.isValid()
    })
}
