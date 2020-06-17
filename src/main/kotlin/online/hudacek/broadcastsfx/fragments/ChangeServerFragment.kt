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

package online.hudacek.broadcastsfx.fragments

import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.events.NotificationEvent
import online.hudacek.broadcastsfx.model.ApiServer
import online.hudacek.broadcastsfx.model.ApiServerModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class ChangeServerFragment : Fragment("Select API server") {

    private val model: ApiServerModel by inject()

    override val root = form()

    init {
        model.item = ApiServer(StationsApi.hostname)

        with(root) {
            setPrefSize(300.0, 110.0)

            fieldset("Set server address") {
                field("URL") {
                    textfield(model.url) {
                        required()
                        validator {
                            if (!model.url.value.contains("."))
                                error("Invalid server address") else null
                        }
                    }
                }
            }
            hbox(5) {
                button("Save") {
                    isDefaultButton = true
                    action {
                        model.commit {
                            fire(NotificationEvent("API server saved!", FontAwesome.Glyph.CHECK))
                            close()
                        }
                    }
                    enableWhen(model.valid)
                }

                button("Cancel") {
                    isCancelButton = true
                    action {
                        close()
                    }
                }
            }
        }
    }
}