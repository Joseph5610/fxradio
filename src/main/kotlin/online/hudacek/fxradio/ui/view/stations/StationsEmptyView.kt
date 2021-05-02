/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package online.hudacek.fxradio.ui.view.stations

import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.StationsState
import online.hudacek.fxradio.viewmodel.StationsViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.glyph

/**
 * This is a view that shows different errors or info messages on stationsView
 */
class StationsEmptyView : BaseView() {

    private val viewModel: StationsViewModel by inject()

    private val searchGlyph by lazy { FontAwesome.Glyph.SEARCH.make() }
    private val errorGlyph by lazy { FontAwesome.Glyph.SEARCH.make() }
    private val noResultsGlyph by lazy { FontAwesome.Glyph.FROWN_ALT.make() }

    private val headerProperty = viewModel.stateProperty.stringBinding {
        when (it) {
            is StationsState.Error -> messages["connectionError"]
            is StationsState.ShortQuery -> messages["searchingLibrary"]
            else -> messages["noResults"]
        }
    }

    private val subHeaderProperty = viewModel.stateProperty.stringBinding {
        when (it) {
            is StationsState.Error -> it.cause
            is StationsState.ShortQuery -> messages["searchingLibraryDesc"]
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

    //Description of a message, shown only if relevant
    private val subHeader by lazy {
        label(subHeaderProperty) {
            id = "stationMessageSubHeader"
            addClass(Styles.grayLabel)
        }
    }

    private val graphic by lazy {
        glyph {
            viewModel.stateObservableChanges()
                    .subscribe {
                        graphicProperty().value = when (it) {
                            is StationsState.Error -> errorGlyph
                            is StationsState.ShortQuery -> searchGlyph
                            else -> noResultsGlyph
                        }
                    }
        }
    }

    override val root = vbox(alignment = Pos.CENTER) {
        paddingTop = 120.0

        add(graphic)
        add(header)
        add(subHeader)

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
