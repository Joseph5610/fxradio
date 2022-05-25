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
