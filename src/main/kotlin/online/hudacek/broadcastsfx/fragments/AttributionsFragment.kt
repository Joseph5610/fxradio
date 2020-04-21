package online.hudacek.broadcastsfx.fragments

import javafx.scene.layout.Priority
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*

class AttributionsFragment : Fragment() {

    private val header = "Third Party software used by " + About.appName

    override val root = vbox {
        title = header

        setPrefSize(500.0, 300.0)
        label(header) {
            addClass(Styles.grayLabel)
        }

        textarea {
            vgrow = Priority.ALWAYS
            text = "macOS installation file background: Designed by xb100 / Freepik"
            style {
                fontFamily = "monospace"
            }
        }
    }
}