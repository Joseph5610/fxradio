package online.hudacek.broadcastsfx.views

import javafx.scene.control.ListView
import online.hudacek.broadcastsfx.controllers.MenuController
import online.hudacek.broadcastsfx.events.StationDirectoryType
import online.hudacek.broadcastsfx.ui.set
import online.hudacek.broadcastsfx.ui.smallLabel
import online.hudacek.broadcastsfx.ui.vboxH
import org.controlsfx.control.textfield.TextFields
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class LeftPaneView : View() {

    private val notification by lazy { find(MainView::class).notification }

    private val listItem = hashMapOf(
            //messages["favourites"] to StationDirectoryType.Favourites,
            messages["topStations"] to StationDirectoryType.TopList)

    private val controller: MenuController by inject()

    private val userMenuItems by lazy { listItem.keys.toList().asObservable() }

    private var libraryListView: ListView<String> by singleAssign()
    private var countriesListView: ListView<String> by singleAssign()

    private val searchField = textfield()

    init {
        runAsync {
            controller
                    .getCountries()
                    .subscribe(
                            { result ->
                                ui {
                                    val results = observableListOf<String>()

                                    result.forEach {
                                        results.add(it.name)
                                    }

                                    TextFields.bindAutoCompletion(searchField, results)

                                    countriesListView = listview(results) {
                                        onUserSelect {
                                            libraryListView.selectionModel.clearSelection()
                                            controller.loadStationsByCountry(it)
                                        }
                                    }
                                    root.add(countriesListView)
                                }
                            }, {
                        ui {
                            notification[FontAwesome.Glyph.WARNING] = messages["downloadError"]
                        }
                    }
                    )
        }
    }

    override val root = vbox {
        paddingAll = 10

        vboxH()
        add(searchField)
        vboxH()
        smallLabel(messages["library"])

        libraryListView = listview(userMenuItems) {
            val size = items.size * 24.0 + 6
            prefHeight = size
            onUserSelect {
                countriesListView.selectionModel.clearSelection()
                controller.loadTopListOfStations()
            }
        }
        vboxH()
        smallLabel(messages["countries"])
    }
}