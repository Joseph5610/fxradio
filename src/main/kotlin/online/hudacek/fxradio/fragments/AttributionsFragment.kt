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
import javafx.scene.layout.Priority
import javafx.stage.StageStyle
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.utils.requestFocusOnSceneAvailable
import online.hudacek.fxradio.viewmodel.AttributionModel
import online.hudacek.fxradio.viewmodel.AttributionViewModel
import online.hudacek.fxradio.viewmodel.Attributions
import tornadofx.*

class AttributionsFragment : Fragment() {

    private val viewModel: AttributionViewModel by inject()

    override val root = vbox {
        title = "${messages["attributions.title"]} ${FxRadio.appName}"
        prefWidth = 500.0

        vbox {
            paddingAll = 10.0
            requestFocusOnSceneAvailable()

            tableview(Attributions.all) {
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
    }

    /**
     * Text Area When user clicks on any attribution
     * to show the contents of license file
     */
    internal class LicenseFragment : Fragment() {

        private val viewModel: AttributionViewModel by inject()

        override val root = vbox {
            setPrefSize(600.0, 400.0)
            titleProperty.bind(viewModel.licenseNameProperty)

            textarea(viewModel.licenseContentProperty) {
                vgrow = Priority.ALWAYS
            }
        }
    }
}