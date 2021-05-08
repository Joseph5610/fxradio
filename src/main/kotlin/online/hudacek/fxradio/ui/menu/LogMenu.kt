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

import online.hudacek.fxradio.viewmodel.Log
import online.hudacek.fxradio.viewmodel.LogViewModel
import org.apache.logging.log4j.Level
import tornadofx.action
import tornadofx.get
import tornadofx.onChange

class LogMenu : BaseMenu("menu.help.loglevel") {

    private val logViewModel: LogViewModel by inject()

    private val checkLoggerOff by lazy {
        checkMenuItem(messages["menu.help.loglevel.off"]) {
            isSelected = logViewModel.item.level == Level.OFF
            action {
                logViewModel.item = Log(Level.OFF)
            }
        }
    }

    private val checkLoggerInfo by lazy {
        checkMenuItem(messages["menu.help.loglevel.info"]) {
            isSelected = logViewModel.item.level == Level.INFO
            action {
                logViewModel.item = Log(Level.INFO)
            }
        }
    }
    private val checkLoggerAll by lazy {
        checkMenuItem(messages["menu.help.loglevel.debug"]) {
            isSelected = logViewModel.item.level == Level.ALL
            action {
                logViewModel.item = Log(Level.ALL)
            }
        }
    }

    init {
        logViewModel.levelProperty.onChange {
            checkLoggerOff.isSelected = it == Level.OFF
            checkLoggerInfo.isSelected = it == Level.INFO
            checkLoggerAll.isSelected = it == Level.ALL
            logViewModel.commit()
        }
    }

    override val menuItems = listOf(checkLoggerOff, checkLoggerInfo, checkLoggerAll)
}
