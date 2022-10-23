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
import javafx.beans.property.ObjectProperty
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.style.AccentColor
import online.hudacek.fxradio.ui.style.Appearance
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Property
import online.hudacek.fxradio.util.macos.MacUtils
import online.hudacek.fxradio.util.reloadStylesheets
import online.hudacek.fxradio.util.saveProperties
import online.hudacek.fxradio.util.value
import tornadofx.booleanProperty
import tornadofx.property

class AppAppearance(
    isDarkMode: Boolean = Properties.DarkMode.value(FxRadio.isDarkModePreferred()),
    accentColor: AccentColor = Appearance.getAccentColor()
) {
    var isDarkMode: Boolean by property(isDarkMode)
    var accentColor: AccentColor by property(accentColor)
}

/**
 * Keeps information about current app appearance
 */
class AppAppearanceViewModel : BaseViewModel<AppAppearance>(AppAppearance()) {

    val darkModeProperty by lazy { bind(AppAppearance::isDarkMode) as BooleanProperty }
    val accentColorProperty by lazy { bind(AppAppearance::accentColor) as ObjectProperty }
    val useSystemColorProperty = booleanProperty(!Property(Properties.AccentColor).isPresent && MacUtils.isMac)


    // Save and Live reload styles
    override fun onCommit() {
        if (useSystemColorProperty.value) {
            Property(Properties.AccentColor).remove()
            accentColorProperty.value = Appearance.getAccentColor()
        } else {
            app.saveProperties(
                mapOf(
                    Properties.DarkMode to darkModeProperty.value,
                    Properties.AccentColor to accentColorProperty.value.colorCode,
                )
            )
        }
        reloadStylesheets(darkModeProperty.value)
    }
}
