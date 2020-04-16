package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import online.hudacek.broadcastsfx.extension.MediaPlayerWrapper
import org.controlsfx.control.NotificationPane
import tornadofx.*
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane

class MainView : View() {

    private val mediaPlayer = MediaPlayerWrapper

    private val appName: String by lazy { messages["appName"] }
    private val playerView: PlayerView by inject()
    private val menuView: MenuView by inject()
    private val stationsView: StationsView by inject()

    private val appIcon: String by lazy { "Election-News-Broadcast-icon.png" }

    var notification: NotificationPane by singleAssign()

    init {
        title = appName
        setStageIcon(Image(appIcon))
    }

    private val rightPane = vbox {
        add(playerView)
        add(stationsView)
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.cancelPlaying()
            }
        }
    }

    override val root = notificationPane {
        notification = this
        isShowFromTop = true

        content {
            vbox {
                vgrow = Priority.ALWAYS
                vbox {
                    setPrefSize(800.0, 600.0)
                    splitpane(Orientation.HORIZONTAL, menuView.root, rightPane) {
                        setDividerPositions(0.3)
                    }
                }
            }
        }
    }
}