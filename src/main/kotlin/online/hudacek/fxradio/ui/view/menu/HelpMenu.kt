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

package online.hudacek.fxradio.ui.view.menu

import javafx.scene.control.CheckMenuItem
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.ui.viewmodel.LogViewModel
import online.hudacek.fxradio.ui.viewmodel.MenuViewModel
import online.hudacek.fxradio.utils.menu
import online.hudacek.fxradio.utils.openUrl
import org.apache.logging.log4j.Level
import tornadofx.*

class HelpMenu : Controller() {

    private val menuViewModel: MenuViewModel by inject()
    private val logViewModel: LogViewModel by inject()

    private var checkLoggerOff: CheckMenuItem by singleAssign()
    private var checkLoggerInfo: CheckMenuItem by singleAssign()
    private var checkLoggerAll: CheckMenuItem by singleAssign()

    private val logsFolderPath = "file://${Config.Paths.baseAppPath}"

    init {
        logViewModel.levelProperty.onChange {
            checkLoggerOff.isSelected = it == Level.OFF
            checkLoggerInfo.isSelected = it == Level.INFO
            checkLoggerAll.isSelected = it == Level.ALL
            logViewModel.levelProperty.value = it
            logViewModel.commit()
        }
    }

    val menu by lazy {
        menu(messages["menu.help"]) {

            item(messages["menu.help.openhomepage"]).action {
                menuViewModel.openWebsite()
            }
            separator()

            item(messages["menu.help.stats"]).action {
                menuViewModel.openStats()
            }

            item(messages["menu.help.clearCache"]).action {
                confirm(messages["cache.clear.confirm"], messages["cache.clear.text"], owner = primaryStage) {
                    menuViewModel.clearCache()
                }
            }

            separator()

            menu(messages["menu.help.loglevel"]) {
                checkLoggerOff = checkmenuitem(messages["menu.help.loglevel.off"]) {
                    action {
                        logViewModel.levelProperty.value = Level.OFF
                    }
                }
                checkLoggerInfo = checkmenuitem(messages["menu.help.loglevel.info"]) {
                    action {
                        logViewModel.levelProperty.value = Level.INFO
                    }
                }
                checkLoggerAll = checkmenuitem(messages["menu.help.loglevel.debug"]) {
                    action {
                        logViewModel.levelProperty.value = Level.ALL
                    }
                }
            }
            item(messages["menu.help.logs"]).action {
                app.openUrl(logsFolderPath)
            }
        }
    }
}
