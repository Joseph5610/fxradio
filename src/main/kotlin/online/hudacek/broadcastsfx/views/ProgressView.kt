package online.hudacek.broadcastsfx.views

import javafx.geometry.Pos
import tornadofx.View
import tornadofx.paddingAll
import tornadofx.progressindicator
import tornadofx.vbox

class ProgressView : View() {

    override val root = vbox {
        alignment = Pos.CENTER

        progressindicator {
            paddingAll = 50.0
        }
    }
}