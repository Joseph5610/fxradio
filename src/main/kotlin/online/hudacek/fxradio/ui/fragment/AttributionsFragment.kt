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
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.util.Attributions
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.viewmodel.Attribution
import online.hudacek.fxradio.viewmodel.AttributionViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.bindSelected
import tornadofx.button
import tornadofx.get
import tornadofx.label
import tornadofx.onUserSelect
import tornadofx.paddingAll
import tornadofx.paddingTop
import tornadofx.prefWidth
import tornadofx.readonlyColumn
import tornadofx.remainingWidth
import tornadofx.smartResize
import tornadofx.tableview
import tornadofx.textarea
import tornadofx.vbox
import tornadofx.vgrow

private const val WINDOW_MIN_WIDTH = 600.0
private const val WINDOW_MIN_HEIGHT = 400.0

class AttributionsFragment : BaseFragment() {

    private val viewModel: AttributionViewModel by inject()

    override val root = vbox {
        title = "${messages["attributions.title"]} ${FxRadio.APP_NAME}"
        prefWidth = 500.0
        paddingAll = 10.0

        requestFocusOnSceneAvailable() // To get rid of the blue box around the table
        tableview(Attributions.list) {
            bindSelected(viewModel)
            readonlyColumn(messages["attributions.name"], Attribution::name).remainingWidth()
            readonlyColumn(messages["attributions.version"], Attribution::version).prefWidth(100)
            smartResize()
            onUserSelect {
                Modal.License.open()
            }
        }

        vbox(alignment = Pos.CENTER_RIGHT) {
            paddingTop = 10.0
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
    class LicenseFragment : BaseFragment() {

        private val viewModel: AttributionViewModel by inject()

        override val root = vbox {
            setPrefSize(WINDOW_MIN_WIDTH, WINDOW_MIN_HEIGHT)
            titleProperty.bind(viewModel.nameProperty)

            vbox(alignment = Pos.CENTER) {
                paddingAll = 10
                label(viewModel.licenseNameProperty) {
                    addClass(Styles.subheader)
                }
                showWhen {
                    viewModel.licenseNameProperty.isNotEmpty
                }
            }

            vbox(alignment = Pos.TOP_LEFT) {
                paddingAll = 5
                vgrow = Priority.ALWAYS

                textarea(viewModel.licenseContentProperty) {
                    paddingAll = 3
                    isEditable = false
                    vgrow = Priority.ALWAYS
                    addClass(Styles.backgroundWhiteSmoke)
                }
            }

            addClass(Styles.backgroundWhite)
        }
    }
}
