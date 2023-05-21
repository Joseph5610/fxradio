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
    val colorCodeKey: String,
) {
    MULTICOLOR(Int.MIN_VALUE, "colors.default"),
    GRAPHITE(-1, "colors.graphite"),
    RED(0, "colors.red"),
    ORANGE(1, "colors.orange"),
    YELLOW(2, "colors.yellow"),
    GREEN(3, "colors.green"),
    BLUE(4, "colors.blue"),
    PURPLE(5, "colors.purple"),
    PINK(6, "colors.pink");

    /**
     * Convert internal representation of accent color into hex
     */
    fun convertToHex(): String = when (this) {
        MULTICOLOR -> "#962cff"
        GRAPHITE -> "#8c8c8c"
        RED -> "#e15257"
        ORANGE -> "#f6821c"
        YELLOW -> "#d09f1c"
        GREEN -> "#62ba46"
        BLUE -> "#007aff"
        PURPLE -> "#a550a7"
        PINK -> "#f750bb"
    }

    companion object {
        val values = values().asList()
    }
}

