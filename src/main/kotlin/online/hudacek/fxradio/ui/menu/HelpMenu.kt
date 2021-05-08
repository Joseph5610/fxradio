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

import online.hudacek.fxradio.Config
import online.hudacek.fxradio.ui.modal.Modals
import online.hudacek.fxradio.ui.modal.open
import online.hudacek.fxradio.ui.openUrl
import tornadofx.action
import tornadofx.get

class HelpMenu : BaseMenu("menu.help") {

    private val logMenu: LogMenu by inject()

    private val logsFolderPath = "file://${Config.Paths.baseAppPath}"

    override val menuItems = listOf(
            item(messages["menu.help.openhomepage"]) {
                action {
                    appMenuViewModel.openWebsite()
                }
            },
            separator(),
            item(messages["menu.help.stats"]) {
                action {
                    Modals.Stats.open()
                }
            },
            item(messages["menu.help.clearCache"]) {
                action {
                    appMenuViewModel.clearCache()
                }
            },
            logMenu.menu,
            separator(),
            item(messages["menu.help.logs"]) {
                action {
                    app.openUrl(logsFolderPath)
                }
            }
    )
}
