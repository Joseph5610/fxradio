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

package online.hudacek.fxradio.ui.style

import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Property
import online.hudacek.fxradio.util.macos.MacUtils

object Colors {
    val palette: Palette by lazy {
        if (FxRadio.isDarkModePreferred()) DarkPalette() else Palette()
    }

    open class Palette {
        val primary by lazy { getPrimaryColor() }
        val transparent = "transparent"

        open val background = "#E9E9E9"
        open val backgroundBorder = "#E8E8E8"
        open val backgroundSelected = "#E9E9E9"
        open val label = "#2b2b2b"
        open val grayLabel = "#8B8B8B"
    }

    class DarkPalette : Palette() {
        override val background = "#333232"
        override val backgroundBorder = "#525356"
        override val backgroundSelected = "#525356"
        override val label = "#ffffff"
        override val grayLabel = "#a0a1a2"
    }

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
}
