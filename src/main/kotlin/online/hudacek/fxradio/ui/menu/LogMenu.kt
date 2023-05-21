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

package online.hudacek.fxradio.ui.menu

import online.hudacek.fxradio.viewmodel.Log
import online.hudacek.fxradio.viewmodel.LogViewModel
import org.apache.logging.log4j.Level
import tornadofx.action
import tornadofx.get
import tornadofx.onChange

class LogMenu : BaseMenu("menu.help.log.title") {

    private val logViewModel: LogViewModel by inject()

    private val checkLoggerOff by lazy {
        checkMenuItem(messages["menu.help.log.off"]) {
            isSelected = logViewModel.item.level == Level.OFF
            action {
                logViewModel.item = Log(Level.OFF)
            }
        }
    }

    private val checkLoggerInfo by lazy {
        checkMenuItem(messages["menu.help.log.info"]) {
            isSelected = logViewModel.item.level == Level.INFO
            action {
                logViewModel.item = Log(Level.INFO)
            }
        }
    }
    private val checkLoggerDebug by lazy {
        checkMenuItem(messages["menu.help.log.debug"]) {
            isSelected = logViewModel.item.level == Level.DEBUG
            action {
                logViewModel.item = Log(Level.DEBUG)
            }
        }
    }

    init {
        logViewModel.levelProperty.onChange {
            checkLoggerOff.isSelected = it == Level.OFF
            checkLoggerInfo.isSelected = it == Level.INFO
            checkLoggerDebug.isSelected = it == Level.DEBUG
            logViewModel.commit()
        }
    }

    override val menuItems = listOf(checkLoggerOff, checkLoggerInfo, checkLoggerDebug)
}
