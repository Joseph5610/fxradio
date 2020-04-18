package online.hudacek.broadcastsfx.fragments

import javafx.geometry.Pos
import online.hudacek.broadcastsfx.StationsApiClient
import tornadofx.*
import tornadofx.controlsfx.infoNotification

class ServerSelectionFragment : Fragment() {

    private val labelTitle = "Select API server"

    override val root = vbox {

        setPrefSize(300.0, 130.0)
        title = labelTitle
        paddingAll = 10.0

        label(labelTitle).paddingBottom = 10.0
        val field = textfield(StationsApiClient.hostname)
        vbox {
            paddingTop = 10.0
            alignment = Pos.CENTER_RIGHT
            button("Save") {
                action {
                    StationsApiClient.hostname = field.text
                    infoNotification(
                            "Saved",
                            "Server saved",
                            position = Pos.TOP_RIGHT)
                }
            }
        }

    }
}