package online.hudacek.broadcastsfx.views

import javafx.geometry.Insets
import javafx.geometry.Pos
import online.hudacek.broadcastsfx.controllers.LibraryController
import online.hudacek.broadcastsfx.model.rest.Countries
import online.hudacek.broadcastsfx.ui.set
import online.hudacek.broadcastsfx.ui.smallLabel
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*
import tornadofx.controlsfx.customTextfield

class LibraryView : View() {

    private val notification by lazy { find(MainView::class).notification }

    private val controller: LibraryController by inject()

    private val retryLink = hyperlink("The list of countries cannot be downloaded. Retry?") {
        isWrapText = true
        hide()
        action {
            controller.getCountries()
        }
    }

    private val libraryListView = listview(controller.libraryItems) {
        prefHeight = items.size * 24.0 + 4
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
        libraryListView.onUserSelect {
            countriesListView.selectionModel.clearSelection()
            controller.loadLibrary(it.type)
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

    fun showCountries(countries: List<Countries>) {
        retryLink.hide()
        countriesListView.show()
        countriesListView.items.setAll(countries)
    }

    fun showError() {
        retryLink.show()
        countriesListView.hide()
        notification[FontAwesome.Glyph.WARNING] = messages["downloadError"]
    }
}