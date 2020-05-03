package online.hudacek.broadcastsfx.fragments

import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.ui.set
import online.hudacek.broadcastsfx.model.ApiServerModel
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.MainView
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class ServerSelectionFragment : Fragment() {

    private val model: ApiServerModel by inject()

    private val notification by lazy { find(MainView::class).notification }

    override val root = Form()

    override fun onBeforeShow() {
        currentStage?.isResizable = false
    }

    init {
        model.url.value = StationsApi.hostname
        title = "Select API server"

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
                    addClass(Styles.primaryButton)
                    action {
                        model.commit {
                            notification[FontAwesome.Glyph.CHECK] = "API Server saved!"
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