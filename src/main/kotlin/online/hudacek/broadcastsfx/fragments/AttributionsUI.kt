package online.hudacek.broadcastsfx.fragments

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.ui.requestFocusOnSceneAvailable
import online.hudacek.broadcastsfx.model.Attribution
import online.hudacek.broadcastsfx.model.AttributionViewModel
import online.hudacek.broadcastsfx.model.Licenses
import tornadofx.*

class LicenseFragment : Fragment() {

    private val model: AttributionViewModel by inject()

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

class AttributionsFragment : Fragment() {

    private val model: AttributionViewModel by inject()

    private val header = "Third Party software used by " + About.appName

    private val attrs = observableListOf(
            Attribution("tornadofx", "1.7.20", Licenses.apache20),
            Attribution("controlsfx", "8.40.16", Licenses.controlsfx),
            Attribution("vlcj", "4.0", Licenses.gpl3),
            Attribution("humble video", "0.3.0", Licenses.gpl3),
            Attribution("Retrofit HTTP client", "2.8.1", Licenses.retrofit),
            Attribution("slf4j-api", "1.7.5", Licenses.sl4fj),
            Attribution("log4j", "2.9.1", Licenses.apache20),
            Attribution("kotlin-logging", "1.7.9", Licenses.apache20),
            Attribution("appdmg", "0.6.0", Licenses.appdmg),
            Attribution("macOS install disk background", "1.0", Licenses.bgLicense)
    )

    override val root = vbox {
        title = header

        setPrefSize(500.0, 300.0)

        vbox {
            paddingAll = 10.0
            requestFocusOnSceneAvailable()

            tableview(attrs) {
                columnResizePolicy = SmartResize.POLICY
                readonlyColumn("Name", Attribution::name).remainingWidth()
                readonlyColumn("Version", Attribution::version)

                bindSelected(model)

                onUserSelect {
                    find<LicenseFragment>().openModal(stageStyle = StageStyle.UTILITY)
                }
            }
        }

        vbox {
            paddingAll = 10.0
            alignment = Pos.CENTER_RIGHT
            button("Close") {
                setOnAction {
                    close()
                }
            }
        }
    }
}