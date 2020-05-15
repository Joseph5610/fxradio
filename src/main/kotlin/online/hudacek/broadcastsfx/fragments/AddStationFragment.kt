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

import javafx.stage.FileChooser
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*
import tornadofx.controlsfx.bindAutoCompletion
import tornadofx.controlsfx.content
import tornadofx.controlsfx.hyperlinklabel
import tornadofx.controlsfx.notificationPane

class AddStationFragment : Fragment("Add new station") {

    override val root = notificationPane {
        prefWidth = 400.0

        content {
            form {
                fieldset("Add new station") {
                    hyperlinklabel("Add station into [radio-browser.info] public directory. Please fill all required information correctly and check if the station has not already been added.") {
                    }


                    field("Name") {
                        textfield {

                        }
                    }
                    field("Website") {
                        textfield {

                        }
                    }
                    field("Stream URL") {
                        textfield {

                        }
                    }
                    field("Icon") {
                        button("Select") {
                            action {
                                val filter = FileChooser.ExtensionFilter("Images (.png, .gif, .jpg)", "*.png", "*.gif", "*.jpg", "*.jpeg")
                                val file = chooseFile("Select Icon", arrayOf(filter))
                                text = if (file.isNotEmpty()) {
                                    "Selected: ${file.last().name}"
                                } else {
                                    "Select"
                                }
                            }
                        }

                    }
                    field("Language") {
                        textfield {

                        }
                    }
                    field("Country") {
                        textfield {

                        }
                    }
                    field("Tags") {
                        textfield {
                            bindAutoCompletion("AAA", "BBB", "CCC")
                        }
                    }
                }

                hbox(5) {
                    button("Save") {
                        isDefaultButton = true
                        addClass(Styles.primaryButton)
                        action {
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