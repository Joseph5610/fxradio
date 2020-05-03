package online.hudacek.broadcastsfx.fragments

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.geometry.Pos
import online.hudacek.broadcastsfx.StationsApiClient
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.requestFocusOnSceneAvailable
import online.hudacek.broadcastsfx.views.ProgressView
import tornadofx.*

/**
 * Modal window that shows status of API server
 */
class StatsFragment : Fragment() {

    private var container = vbox {
        add(ProgressView::class)
    }

    private val stationsApi: StationsApiClient
        get() {
            return StationsApiClient.client
        }

    override fun onBeforeShow() {
        currentStage?.opacity = 0.85
    }

    init {
        title = "Statistics"
        stationsApi.getStats()
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe({

                    val list = observableListOf(
                            "Status: ${it.status}",
                            "API version: ${it.software_version}",
                            "Supported version: ${it.supported_version}",
                            "Stations: ${it.stations}",
                            "Countries: ${it.countries}",
                            "Broken stations: ${it.stations_broken}",
                            "Tags: ${it.tags}")
                    container.replaceChildren(listview(list))
                }, {
                    container.replaceChildren(
                            vbox(alignment = Pos.CENTER) {
                                paddingAll = 20.0
                                label("Stats are not available at the moment.") {
                                    addClass(Styles.header)
                                }
                            }
                    )
                })
    }

    override val root = vbox {
        setPrefSize(300.0, 250.0)
        vbox(alignment = Pos.CENTER) {
            paddingAll = 10.0
            label(StationsApiClient.hostname) {
                requestFocusOnSceneAvailable()
                addClass(Styles.header)
            }
        }
        add(container)
    }
}