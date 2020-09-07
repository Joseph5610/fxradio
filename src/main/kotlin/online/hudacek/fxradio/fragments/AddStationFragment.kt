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

import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.AddStationBody
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.viewmodel.AddStation
import online.hudacek.fxradio.viewmodel.AddStationModel
import tornadofx.*
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane
import java.nio.file.Files

//TODO finish this class properly
//Functionality disabled / not finished
class AddStationFragment : Fragment("Add new station") {

    private val model: AddStationModel by inject()

    private val stationsApi: StationsApi
        get() = StationsApi.client

    override val root = notificationPane {
        prefWidth = 400.0
        model.item = AddStation(AddStationBody())

        content {
            form {
                fieldset("Add new station") {
                    vbox {
                        prefHeight = 50.0
                        vgrow = Priority.ALWAYS
                        label("Add station into radio-browser.info public directory. " +
                                "Please fill all required information correctly and check if" +
                                " the station has not already been added.") {
                            isWrapText = true
                        }
                    }

                    field("Name") {
                        textfield(model.name) {
                            required()
                            validator {
                                if (model.name.length() < 3)
                                    error(messages["invalidAddress"]) else null
                            }
                            promptText = "My Radio Station"
                        }
                    }

                    field("Website") {
                        textfield(model.homepage) {
                            required()
                            validator {
                                if (model.homepage.length() < 3)
                                    error(messages["invalidAddress"]) else null

                            }
                            promptText = "https://example.com/"
                        }
                    }
                    field("Stream URL") {
                        textfield(model.URL) {
                            required()
                            validator {
                                if (model.URL.length() < 3)
                                    error(messages["invalidAddress"]) else null
                            }
                            promptText = "https://example.com/stream.m3u"
                        }
                    }
                    field("Icon") {
                        button("Select") {

                            action {
                                val filter = FileChooser.ExtensionFilter("Images (.png, .gif, .jpg)", "*.png", "*.gif", "*.jpg", "*.jpeg")
                                val file = chooseFile("Select Icon", arrayOf(filter))
                                text = if (file.isNotEmpty() && file.size == 1) {
                                    val mimeType: String = Files.probeContentType(file.first().toPath())
                                    if (mimeType.split("/")[0] == "image") {
                                        "Selected: ${file.last().name}"
                                    } else {
                                        "Invalid file selected."
                                    }
                                } else {
                                    "Select"
                                }
                            }
                        }

                    }
                    field("Language") {
                        textfield(model.language) {
                            required()
                            promptText = "English"
                        }
                    }
                    field("Country") {
                        textfield(model.country) {
                            required()
                            promptText = "United Kingdom"
                        }
                    }
                    field("Tags") {
                        textfield(model.tags) {
                            promptText = "Separated by comma and space"
                        }
                    }
                }

                hbox(5) {
                    button("Save") {
                        enableWhen(model.valid)

                        isDefaultButton = true
                        addClass(Styles.primaryButton)
                        action {
                            model.commit {
                                //stationsApi.add()
                            }
                        }
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
}