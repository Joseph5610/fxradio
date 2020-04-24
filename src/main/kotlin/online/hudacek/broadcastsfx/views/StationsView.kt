package online.hudacek.broadcastsfx.views

import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.controllers.StationsController
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayerTypeChange
import online.hudacek.broadcastsfx.events.StationListReloadEvent
import online.hudacek.broadcastsfx.ui.requestFocusOnSceneAvailable
import online.hudacek.broadcastsfx.ui.set
import online.hudacek.broadcastsfx.ui.tooltip
import online.hudacek.broadcastsfx.model.CurrentStation
import online.hudacek.broadcastsfx.model.StationViewModel
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.createImage
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class StationsView : View() {

    private val notification by lazy { find(MainView::class).notification }

    private val controller: StationsController by inject()
    private val currentStation: StationViewModel by inject()

    init {
        subscribe<StationListReloadEvent> { event ->
            getStations(event.country)
        }

        subscribe<PlayerTypeChange> { event ->
            if (event.changedPlayerType == PlayerType.Native) {
                notification[FontAwesome.Glyph.WARNING] = messages["nativePlayerInfo"]
            }
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
                        selectionModel.selectedItemProperty().onChange {
                            it?.let {
                                currentStation.item = CurrentStation(it)
                            }
                        }

                        cellCache {
                            effect = DropShadow(20.0, Color.LIGHTGRAY)
                            paddingAll = 5
                            vbox(alignment = Pos.CENTER) {
                                tooltip(it)

                                paddingAll = 5

                                imageview {
                                    createImage(this, it)
                                    effect = DropShadow(20.0, Color.LIGHTGRAY)
                                    isCache = true
                                    cacheHint = CacheHint.SPEED
                                    fitHeight = 100.0
                                    fitWidth = 100.0
                                    isPreserveRatio = true
                                    paddingBottom = 10.0
                                }

                                label(it.name)
                            }
                        }
                    }
            )
        }, { notification[FontAwesome.Glyph.WARNING] = messages["downloadError"] })
    }
}