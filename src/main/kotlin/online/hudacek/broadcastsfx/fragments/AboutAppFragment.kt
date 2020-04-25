package online.hudacek.broadcastsfx.fragments

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory
import javafx.geometry.Pos
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.ui.requestFocusOnSceneAvailable
import tornadofx.*
import tornadofx.controlsfx.statusbar

class AboutAppFragment : Fragment() {

    private val list = observableListOf(
            About.appName,
            About.appDesc,
            "",
            About.author,
            About.copyright)

    override fun onBeforeShow() {
        currentStage?.isResizable = false
    }

    override val root = vbox {
        setPrefSize(300.0, 300.0)

        title = "${About.appName} ${About.appVersion}"

        vbox(alignment = Pos.CENTER) {
            paddingAll = 20.0

            imageview(About.appIcon) {
                requestFocusOnSceneAvailable()
                fitHeight = 100.0
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
                                val hostServices = HostServicesFactory.getInstance(app)
                                hostServices.showDocument(About.dataSource)
                            }
                        }
                    })
        }
    }

}