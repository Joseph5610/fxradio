package online.hudacek.broadcastsfx.views

import javafx.scene.control.ListView
import javafx.scene.paint.Color
import javafx.scene.text.Font
import online.hudacek.broadcastsfx.controllers.MenuController
import online.hudacek.broadcastsfx.styles.NoBorder
import tornadofx.*
import java.util.ArrayList

class MenuView : View() {
    private val controller: MenuController by inject()

    override val root = vbox {

        paddingAll = 10

        vbox {
            prefHeight = 20.0
        }

        textfield {
            promptText = "Search"
        }

        vbox {
            prefHeight = 20.0
        }

        label("Library") {
            textFill = Color.GRAY
            font = Font(11.0)
        }

        listview(listOf("Favourites", "Top Stations").asObservable()) {
            prefHeight = items.size * 24.0 + 4
            items.onChange {
                (parent as ListView<*>).setPrefHeight(items.size * 24.0 + 4)
            }
        }

        vbox {
            prefHeight = 20.0
        }
        label("Countries") {
            textFill = Color.GRAY
            font = Font(11.0)
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
            addClass(NoBorder.style)
            onUserSelect(1) {
            }
        })
    }
}