package online.hudacek.broadcastsfx.fragments

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.ui.requestFocusOnSceneAvailable
import online.hudacek.broadcastsfx.model.Attribution
import online.hudacek.broadcastsfx.model.AttributionModel
import online.hudacek.broadcastsfx.model.Attributions
import tornadofx.*

class AttributionsFragment : Fragment() {

    private val model: AttributionModel by inject()

    private val header = "Third Party software used by " + About.appName

    override val root = vbox {
        title = header

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
                setOnAction {
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
            title = model.license.value.name

            textarea {
                vgrow = Priority.ALWAYS
                text = model.license.value.content
                style {
                    fontFamily = "monospace"
                }
            }
        }
    }
}