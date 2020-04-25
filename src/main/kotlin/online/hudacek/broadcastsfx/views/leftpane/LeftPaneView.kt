package online.hudacek.broadcastsfx.views.leftpane

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.geometry.Insets
import javafx.geometry.Pos
import online.hudacek.broadcastsfx.controllers.LeftPaneController
import online.hudacek.broadcastsfx.events.StationListType
import online.hudacek.broadcastsfx.ui.set
import online.hudacek.broadcastsfx.ui.smallLabel
import online.hudacek.broadcastsfx.views.MainView
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*
import tornadofx.controlsfx.customTextfield


class LeftPaneView : View() {

    private val notification by lazy { find(MainView::class).notification }

    private val listItem = hashMapOf(
            //messages["favourites"] to StationDirectoryType.Favourites,
            messages["topStations"] to StationListType.TopStations)

    private val controller: LeftPaneController by inject()

    private val userMenuItems by lazy { observableListOf(listItem.keys) }

    private val retryLink = hyperlink("Retry?") {
        setOnAction {
            getCountries()
        }
    }

    private val libraryListView = listview(userMenuItems) {
        val size = items.size * 24.0 + 6
        prefHeight = size
    }

    private val countriesListView = listview<String> {
        onUserSelect {
            libraryListView.selectionModel.clearSelection()
            controller.loadStationsByCountry(it)
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

        libraryListView.onUserSelect {
            countriesListView.selectionModel.clearSelection()
            controller.loadTopListOfStations()
        }

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
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(
                        { result ->
                            val results = observableListOf<String>()

                            result.forEach {
                                results.add(it.name)
                            }

                            countriesListView.items.setAll(results)
                        },
                        {
                            retryLink.show()
                            notification[FontAwesome.Glyph.WARNING] = messages["downloadError"]
                        }
                )
    }
}