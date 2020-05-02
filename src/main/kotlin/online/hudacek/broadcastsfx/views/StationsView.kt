package online.hudacek.broadcastsfx.views

import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.effect.DropShadow
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.controllers.StationsController
import online.hudacek.broadcastsfx.events.*
import online.hudacek.broadcastsfx.fragments.StationInfoFragment
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.model.StationHistoryModel
import online.hudacek.broadcastsfx.model.CurrentStationModel
import online.hudacek.broadcastsfx.ui.set
import online.hudacek.broadcastsfx.ui.tooltip
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.createImage
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.showPopover

/**
 * Main view displaying grid of stations
 */
class StationsView : View() {

    //notifications pane exists on main view, so we need to find it somehow
    private val notification by lazy { find(MainView::class).notification }

    private val controller: StationsController by inject()
    private val currentStation: CurrentStationModel by inject()
    private val stationHistory: StationHistoryModel by inject()

    private val header = label {
        addClass(Styles.header)
    }

    private val headerContainer = vbox(alignment = Pos.CENTER) {
        paddingTop = 120.0
        add(header)
    }

    private val contentContainer = vbox()

    init {
        controller.getTopStations()
        subscribe<LibraryRefreshEvent> { event ->
            with(event) {
                when (type) {
                    LibraryType.Country -> {
                        params?.let { controller.getStationsByCountry(it) }
                    }
                    LibraryType.History -> getHistory()
                    else -> {
                        controller.getTopStations()
                    }
                }
            }
        }

        subscribe<LibrarySearchChanged> { event ->
            with(event) {
                if (searchString.isEmpty()) {
                    headerContainer.show()
                    val graph = Glyph("FontAwesome", FontAwesome.Glyph.SEARCH)
                    graph.size(35.0)
                    graph.padding = Insets(10.0, 5.0, 10.0, 5.0)
                    header.graphic = graph
                    header.text = "Searching the library"
                    contentContainer.hide()
                } else {
                    if (searchString.length > 3)
                        controller.searchStations(searchString)
                }
            }
        }
    }

    override val root = vbox {
        vgrow = Priority.ALWAYS
        add(headerContainer)
        add(contentContainer)
    }

    private fun getHistory() {
        showDataGrid(stationHistory.stations.value)
    }

    fun showNotification() {
        notification[FontAwesome.Glyph.WARNING] = messages["downloadError"]
    }

    fun showDataGrid(observableList: ObservableList<Station>) {
        headerContainer.hide()
        contentContainer.show()
        contentContainer.replaceChildren(
                datagrid(observableList) {
                    fitToParentHeight()
                    bindSelected(currentStation.station)

                    cellCache {
                        paddingAll = 5
                        vbox(alignment = Pos.CENTER) {
                            popover {
                                vbox {
                                    add(StationInfoFragment(it))
                                }
                            }

                            onRightClick {
                                showPopover()
                            }

                            tooltip(it)
                            paddingAll = 5
                            vbox(alignment = Pos.CENTER) {
                                prefHeight = 120.0
                                paddingAll = 5
                                imageview {
                                    createImage(it)
                                    effect = DropShadow(15.0, Color.LIGHTGRAY)
                                    isCache = true
                                    cacheHint = CacheHint.SPEED
                                    fitHeight = 100.0
                                    fitWidth = 100.0
                                    isPreserveRatio = true
                                }
                            }
                            label(it.name)
                        }
                    }
                }
        )
    }
}