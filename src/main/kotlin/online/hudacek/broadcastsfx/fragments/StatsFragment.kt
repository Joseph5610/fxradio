package online.hudacek.broadcastsfx.fragments

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.schedulers.Schedulers
import javafx.geometry.Pos
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.extension.openUrl
import online.hudacek.broadcastsfx.extension.requestFocusOnSceneAvailable
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.ProgressView
import tornadofx.*

/**
 * Modal window that shows status of API server
 */
class StatsFragment : Fragment("Statistics") {

    private var container = vbox {
        add(ProgressView::class)
    }

    private val stationsApi: StationsApi
        get() {
            return StationsApi.client
        }

    override fun onBeforeShow() {
        currentStage?.opacity = 0.85
    }

    init {
        stationsApi.getStats()
                .subscribeOn(Schedulers.io())
                .observeOnFx()
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
                            label("Stats are not available at the moment.") {
                                paddingAll = 20.0
                            }
                    )
                })
    }

    override val root = vbox {
        setPrefSize(300.0, 250.0)
        vbox(alignment = Pos.CENTER) {
            requestFocusOnSceneAvailable()

            hyperlink(StationsApi.hostname) {
                paddingAll = 10.0
                addClass(Styles.header)

                action {
                    app.openUrl("http://${this.text}")
                }
            }
            add(container)
        }
    }
}