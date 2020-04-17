package online.hudacek.broadcastsfx.views

import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.controllers.StationsController
import online.hudacek.broadcastsfx.events.StationListReloadEvent
import online.hudacek.broadcastsfx.extension.requestFocusOnSceneAvailable
import online.hudacek.broadcastsfx.extension.set
import online.hudacek.broadcastsfx.extension.tooltip
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class StationsView : View() {

    private val controller: StationsController by inject()
    private val notification by lazy { find(MainView::class).notification }
    private val cloudsImageView by lazy { "Clouds-icon.png" }

    init {
        subscribe<StationListReloadEvent> { event ->
            getStations(event.country)
        }
    }

    override val root = vbox(alignment = Pos.CENTER) {
        label(messages["startScreen"]) {
            isWrapText = true
            requestFocusOnSceneAvailable()
            addClass(Styles.playerStationInfo)
        }
    }

    private fun getStations(country: String = "") {
        controller.getStationsByCountry(country).subscribe({ result ->
            root.replaceChildren(
                    datagrid(result.asObservable()) {
                        cellCache {
                            effect = DropShadow(15.0, Color.LIGHTGRAY)
                            paddingAll = 5
                            vbox(alignment = Pos.CENTER) {
                                requestFocusOnSceneAvailable()
                                tooltip(it)

                                paddingAll = 5

                                if (it.favicon == null || it.favicon!!.isEmpty()) {
                                    it.favicon = cloudsImageView
                                }

                                imageview(it.favicon, lazyload = true) {
                                    isCache = true
                                    cacheHint = CacheHint.SPEED
                                    fitHeight = 100.0
                                    fitWidth = 100.0
                                    isPreserveRatio = true
                                }

                                label(it.name)

                                if (it.url_resolved != null) {
                                    onUserSelect(clickCount = 2) {
                                        controller.playStream(it)
                                    }
                                } else {
                                    notification[FontAwesome.Glyph.WARNING] = messages["downloadError"]
                                }
                            }
                        }
                    }
            )
        }, { notification[FontAwesome.Glyph.WARNING] = messages["downloadError"] })
    }
}