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

import online.hudacek.fxradio.StationsApi
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.model.ApiServer
import online.hudacek.fxradio.model.ApiServerModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class ChangeServerFragment : Fragment() {

    private val model: ApiServerModel by inject()

    override val root = form()

    init {
        title = messages["title"]
        model.item = ApiServer(StationsApi.hostname)

        with(root) {
            setPrefSize(300.0, 110.0)

            fieldset(messages["title"]) {
                field(messages["url"]) {
                    textfield(model.url) {
                        required()
                        validator {
                            if (!model.url.value.contains("."))
                                error(messages["invalidAddress"]) else null
                        }
                    }
                }
            }
            hbox(5) {
                button(messages["save"]) {
                    isDefaultButton = true
                    action {
                        model.commit {
                            fire(NotificationEvent(messages["serverSaved"], FontAwesome.Glyph.CHECK))
                            close()
                        }
                    }
                    enableWhen(model.valid)
                }

                button(messages["cancel"]) {
                    isCancelButton = true
                    action {
                        close()
                    }
                }
            }
        }
    }
}