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

package online.hudacek.fxradio.ui.menu

import javafx.beans.property.Property
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCodeCombination
import online.hudacek.fxradio.api.model.Station
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