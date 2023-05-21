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
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.openUrl
import online.hudacek.fxradio.ui.util.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.util.smallLabel
import tornadofx.action
import tornadofx.addClass
import tornadofx.get
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingBottom
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

            smallLabel(messages["about.appDescription"]) {
                style {
                    paddingBottom = 10.0
                }
            }

            smallLabel("${FxRadio.copyright} ${FxRadio.author}")
        }

        vbox(alignment = Pos.CENTER) {
            paddingAll = 10.0

            hyperlink(messages["about.dataSource"]) {
                action {
                    app.openUrl(Config.API.radioBrowserUrl)
                }
                addClass(Styles.grayLabel)
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}
