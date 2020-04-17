package online.hudacek.broadcastsfx.fragments

import javafx.geometry.Pos
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.label
import tornadofx.vbox

class AboutAppFragment : Fragment() {

    override val root = vbox {
        setPrefSize(120.0, 120.0)
        alignment = Pos.CENTER
        label("About app") {
            addClass(Styles.playerStationInfo)
        }
    }

}