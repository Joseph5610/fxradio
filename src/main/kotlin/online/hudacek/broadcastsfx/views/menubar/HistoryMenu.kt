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

package online.hudacek.broadcastsfx.views.menubar

import com.sun.javafx.PlatformUtil
import javafx.scene.control.Menu
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.extension.createImage
import online.hudacek.broadcastsfx.extension.shouldBeDisabled
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.model.StationsHistoryModel
import tornadofx.*

class HistoryMenu : Component() {

    private val stationsHistory: StationsHistoryModel by inject()
    private val player: PlayerModel by inject()

    private val usePlatformMenuBarProperty = app.config.boolean(Config.Keys.useNativeMenuBar, true)

    val menu = Menu(messages["menu.history"]).apply {
        shouldBeDisabled(player.stationProperty)
        items.bind(stationsHistory.stations) {
            item("${it.name} (${it.countrycode})") {
                //for some reason macos native menu does not respect
                //width/height setting so it is disabled for now
                if (!PlatformUtil.isMac() || !usePlatformMenuBarProperty) {
                    graphic = imageview {
                        createImage(it)
                        fitHeight = 15.0
                        fitWidth = 15.0
                        isPreserveRatio = true
                    }
                }
                action {
                    player.stationProperty.value = it
                }
            }
        }
    }
}