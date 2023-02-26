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

package online.hudacek.fxradio.util.macos

import airsquared.JMacNotification.NSUserNotification
import online.hudacek.fxradio.util.Command
import org.controlsfx.tools.Platform
import tornadofx.FX.Companion.messages
import tornadofx.get

object MacUtils {

    val isMac = Platform.getCurrent() == Platform.OSX

    /**
     * Shows macOS native system notification
     */
    fun notification(title: String, subtitle: String) =
        NSUserNotification().apply {
            this.title = title
            this.informativeText = subtitle
            this.actionButtonTitle = messages["show"]
        }.show()

    val isSystemDarkMode: Boolean
        get() = Command("defaults read -g AppleInterfaceStyle").exec() == "Dark"

    val systemAccentColor: Int
        get() = Command("defaults read -g AppleAccentColor").exec().toIntOrNull() ?: Int.MIN_VALUE
}
