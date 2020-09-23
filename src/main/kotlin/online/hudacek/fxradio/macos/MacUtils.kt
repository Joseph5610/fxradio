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

package online.hudacek.fxradio.macos

import airsquared.JMacNotification.NSUserNotification
import de.codecentric.centerdevice.MenuToolkit
import org.controlsfx.tools.Platform
import java.util.*

object MacUtils {

    val isMac = Platform.getCurrent() == Platform.OSX

    //MacOS native notification
    fun notification(title: String, subtitle: String) =
            NSUserNotification().apply {
                this.title = title
                this.informativeText = subtitle
                show()
            }

    //NSMenu toolkit
    fun menuToolkit(): MenuToolkit = MenuToolkit.toolkit(Locale.getDefault())
}