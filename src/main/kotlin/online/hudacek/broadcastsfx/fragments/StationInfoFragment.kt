package online.hudacek.broadcastsfx.fragments

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory
import online.hudacek.broadcastsfx.model.StationViewModel
import tornadofx.*


class StationInfoFragment : Fragment() {

    val currentStation: StationViewModel by inject()

    override val root = vbox {
        setPrefSize(200.0, 200.0)

        currentStation.station.value?.let {
            title = currentStation.station.value.name

            label(currentStation.station.value.name)
            label(currentStation.station.value.tags)
            label(currentStation.station.value.codec)
            label(currentStation.station.value.language)
            label("Last checked: " + currentStation.station.value.lastcheckok)
            hyperlink(currentStation.station.value.homepage).action {
                val hostServices = HostServicesFactory.getInstance(app)
                hostServices.showDocument(currentStation.station.value.homepage)
            }
        }

    }
}