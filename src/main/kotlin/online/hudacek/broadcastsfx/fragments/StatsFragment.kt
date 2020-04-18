package online.hudacek.broadcastsfx.fragments

import javafx.scene.layout.VBox
import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.views.ProgressView
import tornadofx.*

class StatsFragment : Fragment() {

    private var container: VBox by singleAssign()
    private val stationsApi: StationsApiClient
        get() {
            return StationsApiClient.client
        }

    init {
        runAsync {
            stationsApi.getStats().subscribe {
                ui { _ ->
                    val list = observableListOf(
                            "Status: ${it.status}",
                            "API version: ${it.software_version}",
                            "Stations: ${it.stations}",
                            "Countries: ${it.countries}",
                            "Broken stations: ${it.stations_broken}")
                    container.replaceChildren(listview(list))
                }
            }
        }
    }

    override val root = vbox {
        setPrefSize(300.0, 300.0)
        container = vbox {
            add(ProgressView::class)
        }
    }
}