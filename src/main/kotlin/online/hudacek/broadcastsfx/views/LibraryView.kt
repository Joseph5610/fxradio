package online.hudacek.broadcastsfx.views

import javafx.geometry.Insets
import javafx.geometry.Pos
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.controllers.LibraryController
import online.hudacek.broadcastsfx.model.rest.Countries
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.smallLabel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.customTextfield
import tornadofx.controlsfx.glyph

class LibraryView : View() {

    private val controller: LibraryController by inject()

    private val retryLink = hyperlink(messages["downloadRetry"]) {
        hide()
        action {
            controller.getCountries()
        }
    }

    private val libraryListView = listview(controller.libraryItems) {
        prefHeight = items.size * 24.0 + 16

        cellFormat {
            padding = Insets(5.0, 10.0, 5.0, 15.0)
            graphic = glyph("FontAwesome", item.graphic)
            text = item.name
            addClass(Styles.customListItem)
        }
        addClass(Styles.noBorder)
    }

    private val countriesListView = listview<Countries> {
        cellFormat {
            padding = Insets(5.0, 10.0, 5.0, 15.0)
            text = "${item.name} (${item.stationcount})"
            addClass(Styles.customListItem)
        }

        addClass(Styles.noBorder)
        onUserSelect(1) {
            libraryListView.selectionModel.clearSelection()
            controller.loadStationsByCountry(it.name)
        }
    }

    private val searchField = customTextfield {
        promptText = messages["search"]

        left = label {
            graphic = glyph("FontAwesome", FontAwesome.Glyph.SEARCH) {
                padding = Insets(10.0, 5.0, 10.0, 5.0)
            }
        }
        val savedQuery = app.config.string(Config.Keys.searchQuery)

        savedQuery?.let {
            if (it.isNotBlank()) {
                text = savedQuery
            }
        }

        textProperty().onChange {
            it?.let {
                if (it.length > 80) {
                    text = it.substring(0, 80)
                } else {
                    controller.searchStation(it.trim())
                }
            }

            with(app.config) {
                set(Config.Keys.searchQuery to text)
                save()
            }
        }

        setOnMouseClicked {
            controller.searchStation(text)
        }
    }

    init {
        libraryListView.onUserSelect(1) {
            countriesListView.selectionModel.clearSelection()
            controller.loadLibrary(it.type)
        }
    }

    override val root = vbox {
        vbox {
            prefHeight = 20.0
        }
        vbox {
            paddingAll = 10.0
            add(searchField)
        }

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
    }
}