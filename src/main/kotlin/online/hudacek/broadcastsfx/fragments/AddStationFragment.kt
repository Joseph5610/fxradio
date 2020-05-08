package online.hudacek.broadcastsfx.fragments

import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*
import tornadofx.controlsfx.bindAutoCompletion
import tornadofx.controlsfx.content
import tornadofx.controlsfx.hyperlinklabel
import tornadofx.controlsfx.notificationPane

class AddStationFragment : Fragment() {

    override val root = notificationPane {
        title = "Add new station"
        prefWidth = 400.0

        content {
            form {
                fieldset("Add new station") {
                    hyperlinklabel("Add station into [radio-browser.info] public directory. Please fill all required information correctly and check if the station has not already been added.") {
                    }


                    field("Name") {
                        textfield() {

                        }
                    }
                    field("Website") {
                        textfield() {

                        }
                    }
                    field("Stream URL") {
                        textfield() {

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
                        textfield() {

                        }
                    }
                    field("Country") {
                        textfield() {

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