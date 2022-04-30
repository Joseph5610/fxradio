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

package online.hudacek.fxradio.viewmodel

import javafx.beans.property.BooleanProperty
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.save
import online.hudacek.fxradio.util.value
import tornadofx.property

class DarkMode(darkMode: Boolean = Properties.DarkMode.value(FxRadio.isDarkModePreferred())) {
    var darkMode: Boolean by property(darkMode)
}

/**
 * Keeps information about current logging level chosen in UI
 * Used in [online.hudacek.fxradio.ui.view.menu.MenuBarView]
 */
class DarkModeViewModel : BaseViewModel<DarkMode>(DarkMode()) {
    val darkModeProperty = bind(DarkMode::darkMode) as BooleanProperty


    override fun onCommit() {
        //Save it
        Properties.DarkMode.save(darkModeProperty.value)
    }
}