/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.ui.fragment

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import online.hudacek.fxradio.apiclient.radiobrowser.model.Country
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.menu.item
import online.hudacek.fxradio.ui.menu.platformContextMenu
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.ListViewHandler
import online.hudacek.fxradio.ui.util.flagIcon
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.searchField
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.controlsfx.glyph
import tornadofx.get
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.imageview
import tornadofx.insets
import tornadofx.label
import tornadofx.listview
import tornadofx.objectBinding
import tornadofx.onChange
import tornadofx.onLeftClick
import tornadofx.paddingAll
import tornadofx.region
import tornadofx.selectedItem
import tornadofx.stringBinding
import tornadofx.style
import tornadofx.vbox

private const val GLYPH_SIZE = 14.0

class CountriesSearchFragment : BaseFragment() {

    private val viewModel: LibraryViewModel by inject()

    private val filteredCountriesList by lazy {
        viewModel.countriesProperty.filtered { _ -> true }
    }

    override val root = vbox {
        vbox {
            paddingAll = 10.0
            searchField(messages["search"], viewModel.countriesQueryProperty) {
                left = FontAwesome.Glyph.SEARCH.make(GLYPH_SIZE, isPrimary = false) {
                    alignment = Pos.CENTER
                    padding = insets(5, 9)
                }

                textProperty().onChange {
                    if (it.isNullOrEmpty()) {
                        filteredCountriesList.setPredicate { _ -> true }
                    } else {
                        filteredCountriesList.setPredicate { c -> c.name.contains(it, ignoreCase = true) }
                    }
                }
            }
        }

        listview(filteredCountriesList) {
            id = "countriesSearchFragment"
            title = messages["countries"]
            setPrefSize(400.0, 400.0)

            val handler = ListViewHandler(this)
            setOnKeyPressed(handler::handle)

            cellCache {
                hbox(spacing = 5, alignment = Pos.CENTER_LEFT) {

                    imageview {
                        image = it.flagIcon
                    }

                    label(it.name.split("(")[0])

                    region {
                        hgrow = Priority.ALWAYS
                    }

                    label("${it.stationCount}") {
                        addClass(Styles.listItemTag)
                    }

                    glyph {
                        val graphic = viewModel.pinnedProperty.objectBinding { l ->
                            val fa = if (l?.contains(it)!!)
                                FontAwesome.Glyph.BOOKMARK
                            else
                                FontAwesome.Glyph.BOOKMARK_ALT

                            fa.make(GLYPH_SIZE) {
                                style {
                                    textFill = Color.WHITESMOKE
                                }
                            }
                        }
                        graphicProperty().bind(graphic)
                        setOnMouseClicked { _ ->
                            togglePin(it)
                        }
                    }

                    platformContextMenu(
                        listOf(item(messages["pin"]) {
                            val itemName = viewModel.pinnedProperty.stringBinding { l ->
                                if (l?.contains(it)!!)
                                    messages["unpin"]
                                else
                                    messages["pin"]
                            }
                            textProperty().bind(itemName)

                            action {
                                togglePin(it)
                            }
                        })
                    )
                }
            }

            cellFormat {
                addClass(Styles.decoratedListItem)
            }

            onLeftClick(clickCount = 2) {
                selectedItem?.let {
                    viewModel.stateProperty.value = LibraryState.SelectedCountry(it)
                    close()
                }
            }

            addClass(Styles.decoratedListView)
        }

        hbox(spacing = 5, alignment = Pos.CENTER_RIGHT) {
            paddingAll = 5
            button(messages["cancel"]) {
                isCancelButton = true
                action {
                    close()
                }
            }
        }
        addClass(Styles.backgroundWhite)
    }

    private fun togglePin(country: Country) {
        if (viewModel.pinnedProperty.contains(country)) {
            viewModel.unpinCountry(country)
        } else {
            viewModel.pinCountry(country)
        }
    }
}

