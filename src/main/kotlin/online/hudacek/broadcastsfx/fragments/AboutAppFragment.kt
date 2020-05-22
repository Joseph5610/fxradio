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

package online.hudacek.broadcastsfx.fragments

import javafx.geometry.Pos
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.Broadcasts
import online.hudacek.broadcastsfx.extension.openUrl
import online.hudacek.broadcastsfx.extension.requestFocusOnSceneAvailable
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*
import tornadofx.controlsfx.statusbar

class AboutAppFragment : Fragment("${About.appName} ${Broadcasts.version}") {

    override val root = vbox {
        prefWidth = 300.0

        vbox(alignment = Pos.CENTER) {
            paddingAll = 20.0

            imageview(About.appLogo) {
                requestFocusOnSceneAvailable()
                fitHeight = 100.0
                isPreserveRatio = true
            }
            label("${About.appName} - ${About.appDesc}")
            label("${About.copyright} ${About.author}") {
                addClass(Styles.grayLabel)
            }
        }

        statusbar {
            rightItems.add(
                    hbox {
                        alignment = Pos.CENTER_LEFT
                        label("Data source:")
                        hyperlink(About.dataSource) {
                            action {
                                app.openUrl(About.dataSource)
                            }
                        }
                    })
        }
    }
}