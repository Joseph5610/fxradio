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

package online.hudacek.fxradio.ui.util

import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.paint.Color
import online.hudacek.fxradio.util.macos.MacUtils
import kotlin.math.roundToInt

internal fun keyCombination(keyCode: KeyCode) =
    KeyCodeCombination(keyCode, if (MacUtils.isMac) KeyCombination.META_DOWN else KeyCombination.CONTROL_DOWN)

/**
 * Retrieve dominant color from [ImageView]
 */
internal fun ImageView.getDominantColor(): Color {
    val pr = image.pixelReader
    val colCount: MutableMap<Color, Long> = hashMapOf()

    for (x in 0 until image.width.roundToInt()) {
        for (y in 0 until image.height.roundToInt()) {
            val col = pr.getColor(x, y)
            if (colCount.containsKey(col)) {
                colCount[col] = colCount[col]!! + 1
            } else {
                colCount[col] = 1L
            }
        }
    }
    return colCount.maxBy { it.value }.key
}


