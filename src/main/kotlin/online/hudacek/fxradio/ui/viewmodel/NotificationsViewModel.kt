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

package online.hudacek.fxradio.ui.viewmodel

import io.reactivex.subjects.BehaviorSubject
import javafx.beans.property.BooleanProperty
import online.hudacek.fxradio.Properties
import online.hudacek.fxradio.Property
import online.hudacek.fxradio.macos.MacUtils
import tornadofx.ItemViewModel
import tornadofx.property

data class Notification(val title: String, val value: String)

class NotificationsModel(show: Boolean = false) {
    var show: Boolean by property(show)
}

/**
 * Show Native OS notification
 */
class NotificationsViewModel : ItemViewModel<NotificationsModel>(NotificationsModel()) {
    val showProperty = bind(NotificationsModel::show) as BooleanProperty

    val show = BehaviorSubject.create<Notification>()

    init {
        show.filter { showProperty.value && MacUtils.isMac }
                .subscribe {
                    MacUtils.notification(it.title, it.value)
                }
    }

    override fun onCommit() = Property(Properties.NOTIFICATIONS).save(showProperty.value)
}