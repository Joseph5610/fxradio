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

package online.hudacek.fxradio.viewmodel

import javafx.beans.property.BooleanProperty
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.style.Appearance
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.save
import online.hudacek.fxradio.util.value
import tornadofx.cleanBind
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
        // Save it
        Properties.DarkMode.save(darkModeProperty.value)

        // Live reload styles
        Appearance.toggleAppearance(darkModeProperty.value)
    }
}