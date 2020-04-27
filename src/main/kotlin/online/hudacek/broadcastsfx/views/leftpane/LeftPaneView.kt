package online.hudacek.broadcastsfx.views.leftpane

import javafx.geometry.Insets
import javafx.geometry.Pos
import online.hudacek.broadcastsfx.controllers.LeftPaneController
import online.hudacek.broadcastsfx.events.StationListType
import online.hudacek.broadcastsfx.model.Countries
import online.hudacek.broadcastsfx.ui.set
import online.hudacek.broadcastsfx.ui.smallLabel
import online.hudacek.broadcastsfx.views.MainView
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*
import tornadofx.controlsfx.customTextfield

class LeftPaneView : View() {

    private val notification by lazy { find(MainView::class).notification }

    private val controller: LeftPaneController by inject()

    private val retryLink = hyperlink("Retry?") {
        setOnAction {
            getCountries()
        }
    }

    private val libraryListView = listview(controller.libraryItems) {
        val size = items.size * 24.0 + 6
        prefHeight = size
    }

    private val countriesListView = listview<Countries> {
        onUserSelect {
            libraryListView.selectionModel.clearSelection()
            controller.loadStationsByCountry(it.name)
        }
    }

    private val searchField = customTextfield {
        promptText = messages["search"]

        left = label {
            val graph = Glyph("FontAwesome", FontAwesome.Glyph.SEARCH)
            graph.padding = Insets(10.0, 5.0, 10.0, 5.0)
            graphic = graph
        }

        textProperty().onChange {
            it?.let { controller.searchStation(it) }
        }

        setOnMouseClicked {
            controller.searchStation(text)
        }
    }

    init {
        getCountries()

        libraryListView.onUserSelect {
            countriesListView.selectionModel.clearSelection()
            controller.loadTopListOfStations()
        }
    }

    override val root = vbox {
        paddingAll = 10

        vbox {
            prefHeight = 20.0
        }
        add(searchField)
        vbox {
            prefHeight = 20.0
        }
        smallLabel(messages["library"])


        add(libraryListView)
        vbox {
            prefHeight = 20.0
        }

        smallLabel(messages["countries"])
        vbox(alignment = Pos.CENTER) {
            add(retryLink)
            add(countriesListView)
        }
    }

    private fun getCountries() {
        retryLink.hide()
        controller
                .getCountries()
                .subscribe(
                        { countries ->
                            countriesListView.items.setAll(countries)
                        },
                        {
                            retryLink.show()
                            notification[FontAwesome.Glyph.WARNING] = messages["downloadError"]
                        }
                )
    }
}