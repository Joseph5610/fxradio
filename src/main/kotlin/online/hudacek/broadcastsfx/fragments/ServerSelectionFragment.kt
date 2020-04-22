package online.hudacek.broadcastsfx.fragments

import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.model.ApiServerViewModel
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*

class ServerSelectionFragment : Fragment() {

    val model: ApiServerViewModel by inject()

    override val root = Form()

    override fun onBeforeShow() {
        currentStage?.isResizable = false
    }

    init {
        model.url.value = StationsApiClient.hostname
        title = "Select API server"

        with(root) {
            setPrefSize(300.0, 110.0)
            isResizable

            fieldset("Set server address") {
                field("URL") {
                    textfield(model.url) {
                        validator {
                            if (model.url.value.isEmpty()
                                    || !model.url.value.contains("."))
                                error("Invalid server address") else null
                        }
                    }
                }
            }
            hbox(5) {
                button("Save") {
                    addClass(Styles.primaryButton)
                    setOnAction {
                        model.commit()
                        StationsApiClient.hostname = model.url.value
                        close()
                    }
                    disableProperty().bind(model.url.isBlank())
                }

                button("Cancel") {
                    setOnAction {
                        close()
                    }
                }
            }

        }
    }
}