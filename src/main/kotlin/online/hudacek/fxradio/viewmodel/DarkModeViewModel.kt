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
import online.hudacek.fxradio.ui.style.DarkAppearance
import online.hudacek.fxradio.ui.style.LightAppearance
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.reloadStylesheets
import online.hudacek.fxradio.util.save
import online.hudacek.fxradio.util.value
import tornadofx.objectBinding
import tornadofx.property

class DarkMode(isDarkMode: Boolean = Properties.DarkMode.value(FxRadio.isDarkModePreferred())) {
    var isDarkMode: Boolean by property(isDarkMode)
}

/**
 * Keeps information about current logging level chosen in UI
 * Used in [online.hudacek.fxradio.ui.view.MenuBarView]
 */
class DarkModeViewModel : BaseViewModel<DarkMode>(DarkMode()) {

    val darkModeProperty by lazy { bind(DarkMode::isDarkMode) as BooleanProperty }

    val appearanceProperty = darkModeProperty.objectBinding {
        if (darkModeProperty.value) DarkAppearance() else LightAppearance()
    }

    // Save and Live reload styles
    override fun onCommit() = darkModeProperty.value.let {
        Properties.DarkMode.save(it)
        reloadStylesheets(it)
    }
}
