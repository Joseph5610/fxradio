package online.hudacek.broadcastsfx.views

import javafx.collections.ObservableList
import javafx.scene.control.ListView
import mu.KotlinLogging
import online.hudacek.broadcastsfx.controllers.MenuController
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*
import java.util.ArrayList

private val logger = KotlinLogging.logger {}

class MenuView : View() {

    private val controller: MenuController by inject()

    private val userMenuItems: ObservableList<String> by lazy {
        listOf(messages["favourites"], messages["topStations"]).asObservable()
    }

    override val root = vbox {
        paddingAll = 10

        vbox {
            prefHeight = 20.0
        }

        textfield {
            promptText = messages["search"]
        }

        vbox {
            prefHeight = 20.0
        }

        label(messages["library"]) {
            addClass(Styles.grayLabel)
        }

        listview(userMenuItems) {
            prefHeight = items.size * 24.0 + 4
            items.onChange {
                (parent as ListView<*>).setPrefHeight(items.size * 24.0 + 4)
            }
            onUserSelect {
                logger.debug { "selected list item" }
                controller.reloadStations("")
            }
        }

        vbox {
            prefHeight = 20.0
        }

        label(messages["countries"]) {
            addClass(Styles.grayLabel)
        }
    }

    override fun onDock() {
        super.onDock()
        val results = ArrayList<String>()

        controller
                .getCountries()
                .subscribe(
                        { result ->
                            result.forEach {
                                results.add(it.name)
                            }
                        },
                        { error -> error.printStackTrace() }
                )
        root.add(listview(results.asObservable()) {
            onUserSelect {
                logger.debug { "selected $it" }
                controller.reloadStations(it)
            }
        })
    }
}