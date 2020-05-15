package online.hudacek.broadcastsfx.fragments

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.extension.requestFocusOnSceneAvailable
import online.hudacek.broadcastsfx.model.Attribution
import online.hudacek.broadcastsfx.model.AttributionModel
import online.hudacek.broadcastsfx.model.Attributions
import tornadofx.*

class AttributionsFragment : Fragment("Third Party software used by ${About.appName}") {

    private val model: AttributionModel by inject()

    override val root = vbox {
        setPrefSize(500.0, 300.0)

        vbox {
            paddingAll = 10.0
            requestFocusOnSceneAvailable()

            tableview(Attributions.list) {
                columnResizePolicy = SmartResize.POLICY
                readonlyColumn("Name", Attribution::name).remainingWidth()
                readonlyColumn("Version", Attribution::version)

                bindSelected(model)

                onUserSelect {
                    find<LicenseFragment>().openModal(stageStyle = StageStyle.UTILITY)
                }
            }
        }

        vbox(alignment = Pos.CENTER_RIGHT) {
            paddingAll = 10.0
            button("Close") {
                isCancelButton = true
                action {
                    close()
                }
            }
        }
    }

    /**
     * Text Area When user clicks on any attribution
     * to show the contents of license file
     */
    internal class LicenseFragment : Fragment() {

        private val model: AttributionModel by inject()

        override val root = vbox {
            setPrefSize(600.0, 400.0)
            titleProperty.bindBidirectional(model.licenseName)

            textarea {
                bind(model.licenseContent)
                vgrow = Priority.ALWAYS
                style {
                    fontFamily = "monospace"
                }
            }
        }
    }
}