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

package online.hudacek.fxradio.viewmodel

import javafx.application.Platform
import javafx.beans.property.BooleanProperty
import javafx.geometry.Pos
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.macos.MacUtils
import online.hudacek.fxradio.util.save
import online.hudacek.fxradio.util.value
import org.controlsfx.control.Notifications
import tornadofx.property

class StreamTitleNotification(show: Boolean = Properties.SendStreamTitleNotification.value(MacUtils.isMac)) {
    var show: Boolean by property(show)
}

/**
 * Shows Native OS notification when current stream title has changed
 */
class StreamTitleNotificationViewModel : BaseViewModel<StreamTitleNotification>(StreamTitleNotification()) {

    val showProperty = bind(StreamTitleNotification::show) as BooleanProperty

    init {
        appEvent.streamMetaDataUpdates
                .filter { showProperty.value }
                .distinctUntilChanged()
                .subscribe {
                    if (MacUtils.isMac) {
                        MacUtils.notification(it.nowPlaying, it.stationName)
                    } else {
                        Platform.runLater {
                            val notificationBuilder = Notifications.create()
                                    .position(Pos.TOP_RIGHT)
                                    .title(it.stationName)
                                    .text(it.nowPlaying)
                                    //.graphic(ImageView(Image(Config.Resources.appLogo)))
                                    .onAction {
                                        primaryStage.show()
                                    }
                            if (FxRadio.isDarkModePreferred()) {
                                notificationBuilder.darkStyle()
                            }
                            notificationBuilder.show()
                        }
                    }
                }
    }

    override fun onCommit() = Properties.SendStreamTitleNotification.save(showProperty.value)
}
