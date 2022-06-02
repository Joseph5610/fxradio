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

package online.hudacek.fxradio.ui.style

import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Property
import online.hudacek.fxradio.util.macos.MacUtils

class LightAppearance : Appearance() {
    override val background = "#E9E9E9"
    override val backgroundBorder = "#E8E8E8"
    override val backgroundSelected = "#E9E9E9"
    override val label = "#2b2b2b"
    override val grayLabel = "#8B8B8B"
}

class DarkAppearance : Appearance() {
    override val background = "#202121"
    override val backgroundBorder = "#404040"
    override val backgroundSelected = "#525356"
    override val label = "#dddddd"
    override val grayLabel = "#a0a1a2"
    val playerBox = "#464646"
}

abstract class Appearance {

    val primary by lazy { getPrimaryColor() }
    val transparent = "transparent"

    abstract val background: String
    abstract val backgroundBorder: String
    abstract val backgroundSelected: String
    abstract val label: String
    abstract val grayLabel: String

    /**
     * Detects primary color from system accept color
     */
    private fun getPrimaryColor(): String {
        val accentProperty = Property(Properties.AccentColor)
        //Use accent color from app.property file
        val colorCode: Int = if (accentProperty.isPresent) {
            accentProperty.get()
        } else {
            if (MacUtils.isMac) {
                //Use system accent color
                MacUtils.systemAccentColor
            } else {
                //Fallback
                AccentColor.MULTICOLOR.colorCode
            }
        }
        return AccentColor.values().first { it.colorCode == colorCode }.color()
    }

    companion object {
        val currentAppearance by lazy {
            if (FxRadio.isDarkModePreferred()) DarkAppearance() else LightAppearance()
        }
    }
}
