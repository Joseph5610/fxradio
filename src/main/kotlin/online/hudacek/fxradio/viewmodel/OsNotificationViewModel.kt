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

package online.hudacek.fxradio.viewmodel

import javafx.beans.property.BooleanProperty
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import online.hudacek.fxradio.utils.macos.MacUtils
import online.hudacek.fxradio.utils.value
import tornadofx.ItemViewModel
import tornadofx.property

//Notifications are currently enabled only on macOS
class OsNotification(show: Boolean = Properties.SendOsNotifications.value(MacUtils.isMac)) {
    var show: Boolean by property(show)
}

/**
 * Shows Native OS notifications
 */
class OsNotificationViewModel : ItemViewModel<OsNotification>(OsNotification()) {
    private val appEvent: AppEvent by inject()

    val showProperty = bind(OsNotification::show) as BooleanProperty

    init {
        appEvent.streamMetaData
                .filter { showProperty.value && MacUtils.isMac }
                .subscribe {
                    MacUtils.notification(it.nowPlaying, it.stationName)
                }
    }

    override fun onCommit() = Property(Properties.SendOsNotifications).save(showProperty.value)
}