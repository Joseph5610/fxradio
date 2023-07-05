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
package online.hudacek.fxradio.ui.view.stations

import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.util.actionEvents
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.StationsState
import online.hudacek.fxradio.viewmodel.StationsViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.addClass
import tornadofx.booleanBinding
import tornadofx.controlsfx.glyph
import tornadofx.get
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingTop
import tornadofx.stringBinding
import tornadofx.text
import tornadofx.vbox

private const val GLYPH_SIZE = 50.0

/**
 * This is a view that shows different errors or info messages on stationsView
 */
class StationsEmptyView : BaseView() {

    private val viewModel: StationsViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val searchGlyph by lazy { FontAwesome.Glyph.SEARCH.make(size = GLYPH_SIZE, isPrimary = false) }
    private val errorGlyph by lazy { FontAwesome.Glyph.WARNING.make(size = GLYPH_SIZE, isPrimary = false) }
    private val loadingGlyph by lazy { FontAwesome.Glyph.CLOUD_DOWNLOAD.make(size = GLYPH_SIZE, isPrimary = false) }

    private val headerProperty = viewModel.stateProperty.stringBinding {
        when (it) {
            is StationsState.Error -> messages["connectionError.title"]
            is StationsState.ShortQuery -> messages["search.empty.title"]
            is StationsState.Loading -> messages["loading"]
            else -> messages["noResults"]
        }
    }

    private val subHeaderProperty = viewModel.stateProperty.stringBinding {
        when (it) {
            is StationsState.Error -> it.cause
            is StationsState.ShortQuery -> messages["search.empty.description"]
            else -> ""
        }
    }

    private val header by lazy {
        text(headerProperty) {
            id = "stationMessageHeader"
            wrappingWidth = 350.0
            textAlignment = TextAlignment.CENTER

            addClass(Styles.header)
            addClass(Styles.defaultTextColor)
        }
    }

    // Description of a message, shown only if relevant
    private val subHeader by lazy {
        label(subHeaderProperty) {
            id = "stationMessageSubHeader"
            paddingTop = 5.0
            addClass(Styles.grayLabel)
        }
    }

    private val connectionHelpMessage by lazy {
        hyperlink(messages["connectionError.description"]) {
            id = "stationMessageConnectionHelpMsg"
            paddingTop = 5.0

            actionEvents()
                .withLatestFrom(libraryViewModel.stateObservable) { _, s -> s }
                .switchMapMaybe { viewModel.handleNewLibraryState(it) }
                .subscribe(viewModel::show)

            showWhen {
                viewModel.stateProperty.booleanBinding {
                    when (it) {
                        is StationsState.Error -> true
                        else -> false
                    }
                }
            }
            addClass(Styles.grayLabel)
        }
    }

    private val graphic by lazy {
        glyph {
            paddingAll = 10.0
            viewModel.stateObservable
                .subscribe {
                    graphicProperty().value = when (it) {
                        is StationsState.Error -> errorGlyph
                        is StationsState.Loading -> loadingGlyph
                        else -> searchGlyph
                    }
                }
        }
    }

    override val root = vbox(alignment = Pos.CENTER) {
        id = "stationsEmptyView"
        paddingTop = 120.0

        add(graphic)
        add(header)
        add(subHeader)
        add(connectionHelpMessage)

        showWhen {
            viewModel.stateProperty.booleanBinding {
                when (it) {
                    is StationsState.Fetched -> false
                    else -> true
                }
            }
        }
    }
}
