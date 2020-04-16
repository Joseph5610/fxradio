package online.hudacek.broadcastsfx.views

import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.effect.DropShadow
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import mu.KotlinLogging
import online.hudacek.broadcastsfx.controllers.StationsController
import online.hudacek.broadcastsfx.events.StationDirectoryReloadEvent
import online.hudacek.broadcastsfx.extension.requestFocusOnSceneAvailable
import tornadofx.*

private val logger = KotlinLogging.logger {}

class StationsView : View() {

    private val controller: StationsController by inject()
    private lateinit var stationsBox: VBox

    //private val adj = ColorAdjust(0.0, -0.9, -0.5, 0.0)
    //private val blur = GaussianBlur(1.0)
    //adj.input = blur
    //effect = blur

    init {
        subscribe<StationDirectoryReloadEvent> { event ->
            logger.debug { "recived StationDirectoryReloaded event " + event.country }
            getStations(event.country)
        }
    }

    override fun onDock() = getStations()

    override val root = vbox {
        stationsBox = vbox {
            alignment = Pos.CENTER

            vbox {
                paddingAll = 50
            }

            progressindicator()
            vbox {
                paddingAll = 50
            }
            button(messages["retry"]).action {
                getStations()
            }
        }
    }

    private fun getStations(country: String = "") {
        controller.getStationsByCountry(country).subscribe({ result ->
            stationsBox.replaceChildren(
                    datagrid(result.asObservable()) {
                        cellCache {
                            effect = DropShadow(10.0, Color.LIGHTGRAY)
                            paddingAll = 5
                            vbox {
                                requestFocusOnSceneAvailable()
                                onHover { _ ->
                                    tooltip(it.name)
                                }

                                paddingAll = 5
                                alignment = Pos.CENTER

                                it.favicon?.let {
                                    if (it.isNotEmpty()) {
                                        imageview(it, lazyload = true) {
                                            isCache = true
                                            cacheHint = CacheHint.SPEED
                                            fitHeight = 100.0
                                            fitWidth = 100.0
                                            isPreserveRatio = true
                                        }
                                    }
                                }
                                label(it.name) {
                                    isWrapText = true
                                }

                                onUserSelect {
                                    controller.playStream(it)
                                }
                            }
                        }
                    }
            )
        }, { error -> error(messages["downloadError"], error.localizedMessage) })
    }
}