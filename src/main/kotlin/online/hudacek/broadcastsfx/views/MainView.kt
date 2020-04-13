package online.hudacek.broadcastsfx.views

import de.codecentric.centerdevice.MenuToolkit
import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.scene.control.Menu
import javafx.scene.image.Image
import tornadofx.*

class MainView : View() {

    private val playerView: PlayerView by inject()
    private val menuView: MenuView by inject()
    private val stationsView: StationsView by inject()

    init {
        title = "BroadcastsFX"
        setStageIcon(Image("Election-News-Broadcast-icon.png"))
        Platform.runLater {
            val tk = MenuToolkit.toolkit()
            val defaultApplicationMenu: Menu = tk.createDefaultApplicationMenu("BroadcastsFX")
            tk.setApplicationMenu(defaultApplicationMenu)
        }
    }

    private val rightPane = vbox {
        add(playerView)
        add(stationsView)
    }

    override val root = vbox {
        setPrefSize(800.0, 600.0)
        splitpane(Orientation.HORIZONTAL, menuView.root, rightPane) {
            setDividerPositions(0.3)
        }
    }
}