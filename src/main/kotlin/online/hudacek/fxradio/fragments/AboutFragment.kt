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

package online.hudacek.fxradio.fragments

import javafx.geometry.Pos
import javafx.stage.StageStyle
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.styles.Styles
import tornadofx.*
import tornadofx.controlsfx.right
import tornadofx.controlsfx.statusbar

/***
 * Simple Information about the app
 */
class AboutFragment : Fragment(FxRadio.appName) {

    override val root = vbox {
        prefWidth = 300.0

        vbox(alignment = Pos.CENTER) {
            imageview(Config.Resources.appLogo) {
                fitHeight = 80.0
                isPreserveRatio = true
                paddingAll = 20.0
            }
            label(FxRadio.appName + " " + FxRadio.version.version) {
                style {
                    paddingTop = 5.0
                    paddingBottom = 8.0
                }
                addClass(Styles.grayLabel)
            }

            label(messages["about.datasource"]) {
                style {
                    paddingBottom = 8.0
                }
                addClass(Styles.grayLabel)
            }

            label("${FxRadio.copyright} ${FxRadio.author}") {
                addClass(Styles.grayLabel)
            }
        }

        statusbar {
            right {
                hyperlink(messages["menu.app.attributions"]) {
                    action {
                        find<AttributionsFragment>().openModal(stageStyle = StageStyle.UTILITY)
                    }
                }
            }
        }
    }
}