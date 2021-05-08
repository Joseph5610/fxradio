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

package online.hudacek.fxradio.ui.modal

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.Attribution
import online.hudacek.fxradio.viewmodel.AttributionViewModel
import online.hudacek.fxradio.viewmodel.Attributions
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
                readonlyColumn(messages["attributions.version"], Attribution::version)

                onUserSelect {
                    Modals.License.open()
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
                label(viewModel.licenseNameProperty) {
                    addClass(Styles.header)
                }
                showWhen {
                    viewModel.licenseNameProperty.isNotEmpty
                }
            }

            textarea(viewModel.licenseContentProperty) {
                vgrow = Priority.ALWAYS
            }
            addClass(Styles.backgroundWhiteSmoke)
        }
    }
}