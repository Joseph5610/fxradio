package online.hudacek.broadcastsfx.fragments

import javafx.geometry.Pos
import online.hudacek.broadcastsfx.StationsApiClient
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.infoNotification

class ServerSelectionFragment : Fragment() {

    private val urlTextField = textfield()

    override val root = Form()

    init {
        title = "Select API server"

        with(root) {
            setPrefSize(300.0, 110.0)
            fieldset("Set API server") {
                field("URL") {
                    add(urlTextField)
                }
            }
            button("Save") {
                setOnAction {

                }
            }
            //disableProperty().bind()
        }
    }
}