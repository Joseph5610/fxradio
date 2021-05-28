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

package online.hudacek.fxradio.ui.menu

import javafx.scene.control.MenuItem
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import tornadofx.action
import tornadofx.get

class AboutMenu : BaseMenu(FxRadio.appName) {

    val aboutMainItems: List<MenuItem>
        get() = listOf(
                item(messages["menu.app.about"] + " " + FxRadio.appName) {
                    action {
                        Modal.About.open()
                    }
                },
                separator(),
                item(messages["menu.app.server"]) {
                    action {
                        Modal.Servers.open()
                    }
                }
        )

    override val menuItems = mutableListOf<MenuItem>().apply {
        addAll(aboutMainItems)
        addAll(listOf(
                separator(),
                item(messages["menu.app.quit"]) {
                    action {
                        primaryStage.close()
                    }
                }
        ))
    }
}
