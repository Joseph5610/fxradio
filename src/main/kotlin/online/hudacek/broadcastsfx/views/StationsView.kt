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
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.ui.tooltip
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.createImage
import online.hudacek.broadcastsfx.ui.glyph
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.showPopover

/**
 * Main view displaying grid of stations
 */
class StationsView : View() {

    private val controller: StationsController by inject()
    private val playerModel: PlayerModel by inject()
    private val stationHistory: StationHistoryModel by inject()

    private val searchGlyph = glyph(FontAwesome.Glyph.SEARCH)
    private val errorGlyph = glyph(FontAwesome.Glyph.WARNING)

    private val header = label {
        addClass(Styles.header)
    }

    private val subHeader = label {
        addClass(Styles.grayLabel)
    }

    private val headerContainer = vbox(alignment = Pos.CENTER) {
        paddingTop = 120.0
        paddingLeft = 10.0
        paddingRight = 10.0
        add(header)
        add(subHeader)
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
                if (searchString.length > 2)
                    controller.searchStations(searchString)
                else {
                    headerContainer.show()
                    header.text = "Searching the library"
                    header.graphic = searchGlyph
                    subHeader.text = "Enter at least 3 characters to start searching"
                    contentContainer.hide()
                }
            }
        }
    }

    override val root = vbox {

        style {
            backgroundColor += Color.WHITE
        }

        vgrow = Priority.ALWAYS
        add(headerContainer)
        add(contentContainer)
    }

    private fun getHistory() {
        showDataGrid(stationHistory.stations.value)
    }

    fun showNoResults(queryString: String) {
        headerContainer.show()
        contentContainer.hide()
        subHeader.text = "Try refining the search query"
        header.graphic = null
        header.text = "No stations found for \"$queryString\""
    }

    fun showError() {
        headerContainer.show()
        contentContainer.hide()
        header.graphic = errorGlyph
        header.text = "Connection error. Please try again"
        subHeader.text = "Please check if your internet connection is working."
    }

    fun showDataGrid(observableList: ObservableList<Station>) {
        headerContainer.hide()
        contentContainer.show()
        contentContainer.replaceChildren(
                datagrid(observableList) {

                    fitToParentHeight()
                    bindSelected(playerModel.station)

                    cellCache {
                        paddingAll = 5
                        vbox(alignment = Pos.CENTER) {
                            popover {
                                vbox {
                                    add(StationInfoFragment(it, showList = false))
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
                            label(it.name) {
                                style {
                                    textFill = Color.BLACK
                                    fontSize = 14.px
                                }
                            }
                        }
                    }
                }
        )
    }
}