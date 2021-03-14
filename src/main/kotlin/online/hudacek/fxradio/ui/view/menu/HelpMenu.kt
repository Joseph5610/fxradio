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
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.events.AppNotification
import online.hudacek.fxradio.storage.ImageCache
import online.hudacek.fxradio.ui.formatted
import online.hudacek.fxradio.ui.menu
import online.hudacek.fxradio.ui.openUrl
import online.hudacek.fxradio.ui.viewmodel.Log
import online.hudacek.fxradio.ui.viewmodel.LogViewModel
import org.apache.logging.log4j.Level
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class HelpMenu : FxMenu() {
    private val appEvent: AppEvent by inject()

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
            logViewModel.commit()
        }
    }

    override val menu by lazy {
        menu(messages["menu.help"]) {

            item(messages["menu.help.openhomepage"]).action {
                appMenuViewModel.openWebsite()
            }
            separator()

            item(messages["menu.help.stats"]).action {
                appMenuViewModel.openStats()
            }

            item(messages["menu.help.clearCache"]).action {
                if (ImageCache.totalSize < 1) {
                    appEvent.appNotification.onNext(AppNotification(messages["cache.clear.empty"], FontAwesome.Glyph.CHECK))
                } else {
                    confirm(messages["cache.clear.confirm"],
                            messages["cache.clear.text"].formatted(ImageCache.totalSize), owner = primaryStage) {
                        appMenuViewModel.clearCache()
                    }
                }
            }

            separator()

            menu(messages["menu.help.loglevel"]) {
                checkLoggerOff = checkmenuitem(messages["menu.help.loglevel.off"]) {
                    isSelected = logViewModel.levelProperty.value == Level.OFF
                    action {
                        logViewModel.item = Log(Level.OFF)
                    }
                }
                checkLoggerInfo = checkmenuitem(messages["menu.help.loglevel.info"]) {
                    isSelected = logViewModel.levelProperty.value == Level.INFO
                    action {
                        logViewModel.item = Log(Level.INFO)
                    }
                }
                checkLoggerAll = checkmenuitem(messages["menu.help.loglevel.debug"]) {
                    isSelected = logViewModel.levelProperty.value == Level.ALL
                    action {
                        logViewModel.item = Log(Level.ALL)
                    }
                }
            }
            item(messages["menu.help.logs"]).action {
                app.openUrl(logsFolderPath)
            }
        }
    }
}
