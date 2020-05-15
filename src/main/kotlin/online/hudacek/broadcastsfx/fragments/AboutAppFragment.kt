package online.hudacek.broadcastsfx.fragments

import javafx.geometry.Pos
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.Broadcasts
import online.hudacek.broadcastsfx.extension.openUrl
import online.hudacek.broadcastsfx.extension.requestFocusOnSceneAvailable
import tornadofx.*
import tornadofx.controlsfx.statusbar

class AboutAppFragment : Fragment("${About.appName} ${Broadcasts.getVersion()}") {

    private val list = observableListOf(
            About.appName,
            About.appDesc,
            "${About.copyright} ${About.author}")

    override fun onBeforeShow() {
        currentStage?.isResizable = false
    }

    override val root = vbox {
        setPrefSize(300.0, 300.0)

        vbox(alignment = Pos.CENTER) {
            paddingAll = 20.0

            imageview(About.appLogo) {
                requestFocusOnSceneAvailable()
                fitHeight = 100.0
                isPreserveRatio = true
            }
        }

        listview(list)
        statusbar {
            rightItems.add(
                    hbox {
                        alignment = Pos.CENTER_LEFT
                        label("Data source: ")
                        hyperlink(About.dataSource) {
                            action {
                                app.openUrl(About.dataSource)
                            }
                        }
                    })
        }
    }
}