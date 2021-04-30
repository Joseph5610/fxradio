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

import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.viewmodel.AppMenuViewModel
import tornadofx.Controller
import tornadofx.get

abstract class BaseMenu(menuTitle: String) : Controller() {

    protected val appMenuViewModel: AppMenuViewModel by inject()
    protected val appEvent: AppEvent by inject()

    abstract val menuItems: List<MenuItem>

    /**
     * Parent menu object, extending classes defines its items via [menuItems] property
     */
    val menu: Menu by lazy {
        menu(messages[menuTitle]) {
            items.addAll(menuItems)
        }
    }

    /**
     * Defines keyboard shortcuts for menu actions
     */
    protected object KeyCodes {
        val favourite = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)
        val history = KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN)
        val play = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
        val stop = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
        val info = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)
        val add = KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)
    }
}
