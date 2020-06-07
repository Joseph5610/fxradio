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

import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.controllers.menubar.MenuBarController
import online.hudacek.broadcastsfx.events.NotificationEvent
import online.hudacek.broadcastsfx.extension.ui.openUrl
import online.hudacek.broadcastsfx.model.LogLevelModel
import org.apache.logging.log4j.Level
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class HelpMenu : Component() {

    private val controller: MenuBarController by inject()

    private val logLevel: LogLevelModel by inject()

    private var checkLoggerOff: CheckMenuItem by singleAssign()
    private var checkLoggerInfo: CheckMenuItem by singleAssign()
    private var checkLoggerAll: CheckMenuItem by singleAssign()

    init {
        logLevel.level.onChange {
            if (it != null) {
                updateSelectedLoggerLevel(it)
            }
        }
    }

    val menu = Menu(messages["menu.help"]).apply {
        item(messages["menu.help.stats"]).action {
            controller.openStats()
        }
        item(messages["menu.help.clearCache"]).action {
            confirm(messages["cache.clear.confirm"], messages["cache.clear.text"]) {
                if (controller.clearCache()) {
                    fire(NotificationEvent(messages["cache.clear.ok"], FontAwesome.Glyph.CHECK))
                } else {
                    fire(NotificationEvent(messages["cache.clear.error"]))
                }
            }
        }
        separator()
        item(messages["menu.help.openhomepage"]) {
            graphic = imageview("browser-web-icon.png") {
                fitHeight = 15.0
                fitWidth = 15.0
                isPreserveRatio = true
            }
            action {
                controller.openWebsite()
            }
        }
        separator()
        menu(messages["menu.help.loglevel"]) {
            checkLoggerOff = checkmenuitem(messages["menu.help.loglevel.off"]) {
                isSelected = logLevel.level.value == Level.OFF
                action {
                    saveNewLogger(Level.OFF)
                    updateSelectedLoggerLevel(Level.OFF)
                }
            }
            checkLoggerInfo = checkmenuitem(messages["menu.help.loglevel.info"]) {
                isSelected = logLevel.level.value == Level.INFO
                action {
                    saveNewLogger(Level.INFO)
                    updateSelectedLoggerLevel(Level.INFO)
                }
            }
            checkLoggerAll = checkmenuitem(messages["menu.help.loglevel.debug"]) {
                isSelected = logLevel.level.value == Level.DEBUG
                action {
                    saveNewLogger(Level.ALL)
                    updateSelectedLoggerLevel(Level.ALL)
                }
            }
        }
        item(messages["menu.help.logs"]).action {
            app.openUrl("file://${Config.Paths.baseAppDir}")
        }
    }

    private fun saveNewLogger(level: Level) {
        logLevel.level.value = level
        logLevel.commit()
    }

    private fun updateSelectedLoggerLevel(level: Level) {
        checkLoggerOff.isSelected = level == Level.OFF
        checkLoggerInfo.isSelected = level == Level.INFO
        checkLoggerAll.isSelected = level == Level.ALL
    }
}
