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

object Colors {
    val values: ColorValues by lazy {
        if (FxRadio.darkModeEnabled) DarkColorValues() else ColorValues()
    }
}

open class ColorValues {
    open val primary = "#0097CE"
    open val primaryHover = "#0097EA"

    open val background = "#E9E9E9"
    open val backgroundBorder = "#E8E8E8"
    open val backgroundSelected = "#E9E9E9"

    open val label = "#2b2b2b"
    open val grayLabel = "#8B8B8B"

    open val transparent = "transparent"

    open val libraryIcon = "#d65458"
}

class DarkColorValues : ColorValues() {
    override val primary = "#0097CE"
    override val primaryHover = "#0097EA"

    override val background = "#333232"
    override val backgroundBorder = "#525356"
    override val backgroundSelected = "#525356"

    override val label = "#ffffff"
    override val grayLabel = "#a0a1a2"

    override val transparent = "transparent"
}