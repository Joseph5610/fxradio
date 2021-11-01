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

package online.hudacek.fxradio.util

import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import tornadofx.Component
import tornadofx.get

/**
 * Adds system tray. Experimental, enabled under flag
 */
class Tray : Component() {

    fun addIcon() = with(app) {
        if (Config.Flags.useTrayIcon) {
            trayicon(resources.stream("/" + Config.Resources.stageIcon)) {
                setOnMouseClicked(fxThread = true) {
                    primaryStage.show()
                    primaryStage.toFront()
                }
                menu(FxRadio.appName) {
                    item(messages["show"]) {
                        setOnAction(fxThread = true) {
                            primaryStage.show()
                            primaryStage.toFront()
                        }
                    }
                    item(messages["exit"]) {
                        setOnAction(fxThread = true) {
                            primaryStage.close()
                        }
                    }
                }
            }
        }
    }
}