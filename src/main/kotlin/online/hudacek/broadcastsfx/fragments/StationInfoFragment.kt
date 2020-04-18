package online.hudacek.broadcastsfx.fragments

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory
import javafx.geometry.Pos
import online.hudacek.broadcastsfx.model.StationViewModel
import tornadofx.*
import tornadofx.controlsfx.statusbar


class StationInfoFragment : Fragment() {

    private val currentStation: StationViewModel by inject()

    override val root = vbox {
        setPrefSize(300.0, 300.0)

        currentStation.station.value?.let {
            title = it.name

            val codecBitrateInfo = it.codec + " (" + it.bitrate + ")"

            val list = observableListOf(
                    it.tags,
                    codecBitrateInfo,
                    it.country,
                    it.language,
                    it.lastcheckoktime)

            vbox {
                paddingAll = 20.0
                alignment = Pos.CENTER

                imageview(it.favicon) {
                    fitHeight = 100.0
                    fitHeight = 100.0
                    isPreserveRatio = true
                }
            }

            listview(list)
            statusbar {
                rightItems.add(
                        hyperlink(it.homepage) {
                            action {
                                val hostServices = HostServicesFactory.getInstance(app)
                                hostServices.showDocument(it.homepage)
                            }
                        }
                )
            }
        }
    }
}