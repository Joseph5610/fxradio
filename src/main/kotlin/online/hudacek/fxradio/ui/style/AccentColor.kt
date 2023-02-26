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

/**
 * Accent Color definitions
 */
enum class AccentColor(
    val colorCode: Int,
    val humanName: String,
) {
    MULTICOLOR(Int.MIN_VALUE, "App Default"),
    GRAPHITE(-1, "Graphite"),
    RED(0, "Red"),
    ORANGE(1, "Orange"),
    YELLOW(2, "Yellow"),
    GREEN(3, "Green"),
    BLUE(4, "Blue"),
    PURPLE(5, "Purple"),
    PINK(6, "Pink");

    /**
     * Convert internal representation of accent color into hex
     */
    fun convertToHex(): String = when (this) {
        MULTICOLOR -> "#d65458"
        GRAPHITE -> "#8c8c8c"
        RED -> "#e15257"
        ORANGE -> "#f6821c"
        YELLOW -> "#d09f1c"
        GREEN -> "#62ba46"
        BLUE -> "#007aff"
        PURPLE -> "#a550a7"
        PINK -> "#f750bb"
    }
}

