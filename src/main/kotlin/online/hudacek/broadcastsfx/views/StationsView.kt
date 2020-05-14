package online.hudacek.broadcastsfx.views

import javafx.geometry.Pos
import javafx.scene.CacheHint
import javafx.scene.control.Label
import javafx.scene.effect.DropShadow
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import mu.KotlinLogging
import online.hudacek.broadcastsfx.controllers.StationsController
import online.hudacek.broadcastsfx.events.LibraryRefreshEvent
import online.hudacek.broadcastsfx.events.LibrarySearchChanged
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.fragments.StationInfoFragment
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.createImage
import online.hudacek.broadcastsfx.ui.glyph
import online.hudacek.broadcastsfx.ui.tooltip
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.popover
import tornadofx.controlsfx.showPopover

/**
 * Main view displaying grid of stations
 */
class StationsView : View() {

    private val controller: StationsController by inject()
    private val playerModel: PlayerModel by inject()
    private val logger = KotlinLogging.logger {}

    private val searchGlyph = glyph(FontAwesome.Glyph.SEARCH)
    private val errorGlyph = glyph(FontAwesome.Glyph.WARNING)

    private var contentName: Label by singleAssign()

    private val stationsData = observableListOf(Station.stub())

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

    private val dataGrid = datagrid(stationsData) {
        fitToParentHeight()

        selectionModel.selectedItemProperty().onChange {
            //Update model on selected item
            it?.let {
                playerModel.station.value = it
            }
        }

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

    private val contentTop = flowpane {
        paddingBottom = 0.0
        maxHeight = 10.0
        style {
            backgroundColor += Color.WHITESMOKE
        }

        contentName = label {
            paddingTop = 8.0
            paddingBottom = 8.0
            paddingLeft = 15.0
            addClass(Styles.subheader)
        }
    }

    init {
        controller.getTopStations()
        subscribe<LibraryRefreshEvent> { event ->
            with(event) {
                when (type) {
                    LibraryType.Country -> {
                        params?.let { controller.getStationsByCountry(it) }
                    }
                    LibraryType.Favourites -> controller.getFavourites()
                    LibraryType.History -> controller.getHistory()
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
                    contentTop.hide()
                    headerContainer.show()
                    header.text = messages["searchingLibrary"]
                    header.graphic = searchGlyph
                    subHeader.text = messages["searchingLibraryDesc"]
                    dataGrid.hide()
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
        add(contentTop)
        add(dataGrid)
    }

    fun showNoResults(queryString: String? = null) {
        contentTop.hide()
        headerContainer.show()
        dataGrid.hide()
        if (queryString != null) {
            subHeader.text = messages["noResultsDesc"]
        }
        header.graphic = null
        header.text =
                if (queryString != null)
                    "${messages["noResultsFor"]} \"$queryString\""
                else {
                    messages["noResults"]
                }
    }

    fun showError(throwable: Throwable) {
        logger.error(throwable) { "Error showing station library" }
        contentTop.hide()
        headerContainer.show()
        dataGrid.hide()
        header.graphic = errorGlyph
        header.text = messages["connectionError"]
        subHeader.text = messages["connectionErrorDesc"]
    }

    fun showDataGrid(stations: List<Station>) {
        headerContainer.hide()
        contentTop.show()
        dataGrid.show()
        dataGrid.selectionModel.clearSelection()
        stationsData.setAll(stations)
    }

    fun setContentName(libraryType: LibraryType, value: String? = null) {
        contentName.apply {
            text = when (libraryType) {
                LibraryType.Favourites -> messages["favourites"]
                LibraryType.History -> messages["history"]
                LibraryType.TopStations -> messages["topStations"]
                LibraryType.Search -> messages["searchResultsFor"] + " \"$value\""
                else -> value
            }
        }
    }
}