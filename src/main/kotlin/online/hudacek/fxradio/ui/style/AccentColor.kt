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
enum class AccentColor(val colorCode: Int) {
    MULTICOLOR(Int.MIN_VALUE),
    GRAPHITE(-1),
    RED(0),
    ORANGE(1),
    YELLOW(2),
    GREEN(3),
    BLUE(4),
    PURPLE(5),
    PINK(6),
}

/**
 * Maps internal representation of accent color into hex
 */
internal fun AccentColor.color() = when (this) {
    AccentColor.MULTICOLOR -> "#d65458"
    AccentColor.GRAPHITE -> "#8C8C8C"
    AccentColor.RED -> "#FF5258"
    AccentColor.ORANGE -> "#F8821B"
    AccentColor.YELLOW -> "#FFC500"
    AccentColor.GREEN -> "#64B946"
    AccentColor.BLUE -> "#037AFF"
    AccentColor.PURPLE -> "#A550A6"
    AccentColor.PINK -> "#F7509E"
}