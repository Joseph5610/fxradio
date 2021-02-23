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

import javafx.scene.layout.Priority
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import tornadofx.*

class StationDebugFragment : Fragment() {

    private val viewModel: PlayerViewModel by inject()

    override val root = vbox {
        setPrefSize(600.0, 400.0)
        titleProperty.bind(viewModel.stationProperty.asString())

        textarea(viewModel.stationProperty.asString()) {
            vgrow = Priority.ALWAYS
            isWrapText = true
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}