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

package online.hudacek.fxradio.ui.fragment

import javafx.geometry.Pos
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.openUrl
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.smallLabel
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
class AppInfoFragment : BaseFragment() {

    override val root = vbox {
        requestFocusOnSceneAvailable()
        prefWidth = 280.0

        vbox(alignment = Pos.CENTER) {
            imageview(Config.Resources.appLogo) {
                fitHeight = 80.0
                isPreserveRatio = true
                paddingAll = 20.0
            }

            label(FxRadio.appName) {
                style {
                    paddingAll = 10.0
                }
                addClass(Styles.subheader)
            }

            smallLabel(FxRadio.version) {
                style {
                    paddingBottom = 10.0
                }
            }

            smallLabel(FxRadio.appDesc) {
                style {
                    paddingBottom = 10.0
                }
            }

            smallLabel("${FxRadio.copyright} ${FxRadio.author}")
        }

        vbox(alignment = Pos.CENTER_RIGHT) {
            paddingAll = 10.0

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
