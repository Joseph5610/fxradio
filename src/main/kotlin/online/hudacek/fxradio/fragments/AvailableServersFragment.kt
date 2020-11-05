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
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.requestFocusOnSceneAvailable
import online.hudacek.fxradio.viewmodel.ServersViewModel
import tornadofx.*

class AvailableServersFragment : Fragment() {
    private val viewModel: ServersViewModel by inject()

    override fun onDock() {
        runAsync {
            //Load available servers only when we need this
            if (viewModel.serversProperty.isEmpty()) {
                viewModel.serversProperty.value = viewModel.availableServers.distinct().asObservable()
            }
        }
    }

    override val root = vbox {
        title = messages["servers.title"]
        setPrefSize(300.0, 300.0)

        vbox {
            paddingAll = 10.0
            requestFocusOnSceneAvailable()

            listview(viewModel.serversProperty) {
                cellFormat {
                    graphic = label(it) {
                        addClass(Styles.libraryListItem)
                    }
                }
                addClass(Styles.libraryListView)
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
}