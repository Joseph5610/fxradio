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

package online.hudacek.fxradio.ui.fragment

import javafx.geometry.Pos
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.openUrl
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.get
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingBottom
import tornadofx.paddingTop
import tornadofx.style
import tornadofx.vbox

/***
 * Simple Information about the app
 */
class AboutFragment : BaseFragment(FxRadio.appName) {

    override val root = vbox {
        requestFocusOnSceneAvailable()
        prefWidth = 300.0

        vbox(alignment = Pos.CENTER) {
            imageview(Config.Resources.appLogo) {
                fitHeight = 80.0
                isPreserveRatio = true
                paddingAll = 20.0
            }
            label(FxRadio.appName + " " + FxRadio.version) {
                style {
                    paddingTop = 5.0
                    paddingBottom = 8.0
                }
                addClass(Styles.grayLabel)
            }

            label(FxRadio.appDesc) {
                style {
                    paddingBottom = 8.0
                }
                addClass(Styles.grayLabel)
            }

            label("${FxRadio.copyright} ${FxRadio.author}") {
                paddingBottom = 10.0
                addClass(Styles.grayLabel)
            }

            hyperlink(messages["about.datasource"]) {
                action {
                    app.openUrl("http://radio-browser.info")
                }
                addClass(Styles.grayLabel)
            }
        }

        vbox {
            paddingAll = 10.0
            alignment = Pos.CENTER_RIGHT

            button(messages["menu.app.attributions"]) {
                action {
                    Modal.Attributions.open()
                }
                addClass(Styles.primaryButton)
            }
        }

        addClass(Styles.backgroundWhiteSmoke)
    }
}