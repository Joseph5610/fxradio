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
import javafx.scene.layout.Priority
import javafx.stage.StageStyle
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.AttributionModel
import online.hudacek.fxradio.ui.viewmodel.AttributionViewModel
import online.hudacek.fxradio.ui.viewmodel.Attributions
import online.hudacek.fxradio.utils.requestFocusOnSceneAvailable
import tornadofx.*

class AttributionsFragment : Fragment() {

    private val viewModel: AttributionViewModel by inject()

    override val root = vbox {
        title = "${messages["attributions.title"]} ${FxRadio.appName}"
        prefWidth = 500.0

        vbox {
            paddingAll = 10.0
            requestFocusOnSceneAvailable() //To get rid of the blue box around the table
            tableview(Attributions.list) {
                columnResizePolicy = SmartResize.POLICY
                readonlyColumn(messages["attributions.name"], AttributionModel::name).remainingWidth()
                readonlyColumn(messages["attributions.version"], AttributionModel::version)

                bindSelected(viewModel)

                onUserSelect {
                    find<LicenseFragment>().openModal(stageStyle = StageStyle.UTILITY)
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
            titleProperty.bind(viewModel.licenseNameProperty)

            textarea(viewModel.licenseContentProperty) {
                vgrow = Priority.ALWAYS
            }
            addClass(Styles.backgroundWhiteSmoke)
        }
    }
}