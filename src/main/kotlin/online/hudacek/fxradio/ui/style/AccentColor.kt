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