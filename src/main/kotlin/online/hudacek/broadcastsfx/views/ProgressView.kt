package online.hudacek.broadcastsfx.views

import javafx.geometry.Pos
import tornadofx.*

class ProgressView : View() {

    override val root = vbox {
        alignment = Pos.CENTER
        paddingTop = 30.0
        progressindicator()
    }
}