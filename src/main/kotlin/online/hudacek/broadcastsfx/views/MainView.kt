package online.hudacek.broadcastsfx.views

import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import online.hudacek.broadcastsfx.controllers.MainController
import org.controlsfx.control.NotificationPane
import tornadofx.*
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane

class MainView : View() {

    private val controller: MainController by inject()

    private val appName: String by lazy { messages["appName"] }
    private val appIcon: String by lazy { "Election-News-Broadcast-icon.png" }

    private val playerView: PlayerView by inject()
    private val menuView: MenuView by inject()
    private val stationsView: StationsView by inject()

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
            controller.cancelMediaPlaying()
        }
    }

    override val root = vbox {
        setPrefSize(800.0, 600.0)

        menubar {
            menu(appName) {
                item(messages["menu.app.about"]).action {
                    controller.openAbout()
                }
                item(messages["menu.app.quit"]).action {
                    Platform.exit()
                }
            }
            menu(messages["menu.station"]) {
                item(messages["menu.station.info"]).action {
                    controller.openStationInfo()
                }
            }
        }
        notificationPane {
            notification = this
            isShowFromTop = true

            content {
                vbox {
                    vgrow = Priority.ALWAYS
                    vbox {
                        splitpane(Orientation.HORIZONTAL, menuView.root, rightPane) {
                            setDividerPositions(0.3)
                        }
                    }
                }
            }
        }
    }
}