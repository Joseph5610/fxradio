package online.hudacek.broadcastsfx.views

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.controllers.StationsController
import online.hudacek.broadcastsfx.extension.requestFocusOnSceneAvailable
import tornadofx.*

class StationsView : View() {
    private val controller: StationsController by inject()
    private val stationsApi by lazy { controller.stations }

    override val root = vbox {
        alignment = Pos.CENTER
        vbox {
            paddingAll = 50
        }
        progressindicator()
        vbox {
            paddingAll = 50
        }
        button("Retry?").action {
            getStations()
        }
    }

    override fun onDock() {
        super.onDock()
        getStations()
    }

    private fun getStations() {
        stationsApi
                .getTopStations()
                .subscribe({ result ->
                    root.replaceWith(
                            datagrid(result) {
                                println(result)
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
                                                    fitHeight = 100.0
                                                    fitWidth = 100.0
                                                    isPreserveRatio = true
                                                }
                                            }
                                        }
                                        label(it.name) {
                                            isWrapText = true
                                        }
                                        onUserSelect(1) {
                                            controller.playStream(it)
                                        }
                                    }
                                }
                            }
                    )
                }, { error -> error("Unable to get list of stations", error.localizedMessage) })
    }
}