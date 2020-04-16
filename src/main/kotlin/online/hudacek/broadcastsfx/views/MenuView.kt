package online.hudacek.broadcastsfx.views

import javafx.collections.FXCollections
import javafx.scene.control.ListView
import mu.KotlinLogging
import online.hudacek.broadcastsfx.controllers.MenuController
import online.hudacek.broadcastsfx.events.StationDirectoryType
import online.hudacek.broadcastsfx.extension.set
import online.hudacek.broadcastsfx.extension.smallLabel
import online.hudacek.broadcastsfx.extension.vboxH
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

private val logger = KotlinLogging.logger {}

class MenuView : View() {

    private val notification by lazy { find(MainView::class).notification }

    private val listItem = hashMapOf(
            messages["favourites"] to StationDirectoryType.Favourites,
            messages["topStations"] to StationDirectoryType.TopList)

    private val controller: MenuController by inject()

    private val userMenuItems by lazy { listItem.keys.toList().asObservable() }

    private var libraryListView: ListView<String> by singleAssign()
    private var countriesListView: ListView<String> by singleAssign()

    override val root = vbox {
        paddingAll = 10

        vboxH()
        textfield {
            promptText = messages["search"]
        }
        vboxH()
        smallLabel(messages["library"])

        libraryListView = listview(userMenuItems) {
            prefHeight = items.size * 24.0 + 4
            items.onChange {
                (parent as ListView<*>).setPrefHeight(items.size * 24.0 + 4)
            }
            onUserSelect {
                countriesListView.selectionModel.clearSelection()
                controller.loadTopListOfStations()
            }
        }
        vboxH()
        smallLabel(messages["countries"])
    }

    override fun onDock() {
        controller
                .getCountries()
                .subscribe(
                        { result ->
                            val results = FXCollections.observableArrayList<String>()

                            result.forEach {
                                results.add(it.name)
                            }

                            countriesListView = listview(results) {
                                onUserSelect {
                                    libraryListView.selectionModel.clearSelection()
                                    controller.loadStationsByCountry(it)
                                }
                            }
                            add(countriesListView)
                        },
                        { notification[FontAwesome.Glyph.WARNING] = messages["downloadError"] }
                )
    }
}