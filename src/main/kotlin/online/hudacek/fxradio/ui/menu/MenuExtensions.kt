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

import de.jangassen.MenuToolkit
import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.MouseButton
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.util.macos.MacUtils
import tornadofx.bind
import tornadofx.booleanBinding
import tornadofx.contextmenu
import tornadofx.disableWhen

/**
 * Menu helpers
 */
internal fun menu(name: String, op: Menu.() -> Unit = {}) = Menu(name).apply {
    op(this)
}

internal fun item(
    name: String, keyCode: KeyCodeCombination? = null,
    op: MenuItem.() -> Unit = {}
) = MenuItem(name).apply {
    if (keyCode != null) {
        accelerator = keyCode
    }
    op(this)
}

internal fun checkMenuItem(
    name: String, bindProperty: Property<Boolean>? = null,
    keyCode: KeyCodeCombination? = null,
    op: CheckMenuItem.() -> Unit = {}
) = CheckMenuItem(name).apply {
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

internal fun EventTarget.platformContextMenu(menuItems: List<MenuItem>) {
    if (MacUtils.isMac) {
        val menu = Menu().apply {
            items.addAll(menuItems)
        }
        if (this is Node) {
            setOnMouseClicked {
                if (it.button == MouseButton.SECONDARY) {
                    MenuToolkit.toolkit().showContextMenu(menu, it)
                }
            }
        }
    } else {
        contextmenu {
            items.addAll(menuItems)
        }
    }
}