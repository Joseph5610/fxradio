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

import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Property
import online.hudacek.fxradio.util.macos.MacUtils
import tornadofx.Component
import tornadofx.FX
import tornadofx.importStylesheet

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
}

abstract class Appearance : Component() {

    val primary = getAccentColor().convertToHex()

    val transparent = "transparent"
    val playerBox = "#464646"

    abstract val background: String
    abstract val backgroundBorder: String
    abstract val backgroundSelected: String
    abstract val label: String
    abstract val grayLabel: String

    companion object {

        private val accentProperty = Property(Properties.AccentColor)

        /**
         * Detects color from system accent color
         */
        fun getAccentColor(): AccentColor {
            // Use accent color from app.property file
            val systemColorCode: Int = accentProperty.get(
                // Fallback - Use system accent color on macOS
                if (MacUtils.isMac) {
                    MacUtils.systemAccentColor
                } else {
                    // Fallback - primary color for non-mac OS
                    AccentColor.MULTICOLOR.colorCode
                }
            )
            return AccentColor.entries.first { it.colorCode == systemColorCode }
        }

        /**
         * Reload app css styles
         */
        fun reloadStylesheets(isDarkModeProperty: Boolean) {
            val scene = FX.primaryStage.scene
            FX.stylesheets.clear()
            scene.stylesheets.clear()

            if (isDarkModeProperty) {
                importStylesheet(StylesDark::class)
            } else {
                importStylesheet(Styles::class)
            }
            FX.applyStylesheetsTo(scene)
        }
    }
}
