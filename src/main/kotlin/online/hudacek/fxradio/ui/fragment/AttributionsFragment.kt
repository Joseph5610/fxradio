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
import javafx.scene.layout.Priority
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.util.Attributions
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.viewmodel.Attribution
import online.hudacek.fxradio.viewmodel.AttributionViewModel
import tornadofx.*

class AttributionsFragment : BaseFragment() {

    private val viewModel: AttributionViewModel by inject()

    override val root = vbox {
        title = "${messages["attributions.title"]} ${FxRadio.appName}"
        prefWidth = 500.0

        vbox {
            paddingAll = 10.0
            requestFocusOnSceneAvailable() //To get rid of the blue box around the table
            tableview(Attributions.list) {
                bindSelected(viewModel)

                columnResizePolicy = SmartResize.POLICY
                readonlyColumn(messages["attributions.name"], Attribution::name).remainingWidth()
                readonlyColumn(messages["attributions.version"], Attribution::version).prefWidth(100)

                onUserSelect {
                    Modal.License.open()
                }
            }
        }

        vbox(alignment = Pos.CENTER_RIGHT) {
            paddingAll = 10.0
            button(messages["close"]) {
                isCancelButton = true
                action {
                    close()
                }
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }

    /**
     * Text Area When user clicks on any attribution
     * to show the contents of license file
     */
    class LicenseFragment : Fragment() {

        private val viewModel: AttributionViewModel by inject()

        override val root = vbox {
            setPrefSize(600.0, 400.0)
            titleProperty.bind(viewModel.nameProperty)

            vbox(alignment = Pos.CENTER) {
                paddingAll = 10
                label(viewModel.licenseNameProperty)
                showWhen {
                    viewModel.licenseNameProperty.isNotEmpty
                }
                addClass(Styles.backgroundWhiteSmoke)
            }

            textarea(viewModel.licenseContentProperty) {
                vgrow = Priority.ALWAYS
            }
            addClass(Styles.backgroundWhiteSmoke)
        }
    }
}