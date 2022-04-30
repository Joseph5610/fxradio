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
import online.hudacek.fxradio.util.Modal
import online.hudacek.fxradio.util.open
import online.hudacek.fxradio.viewmodel.StationsState
import online.hudacek.fxradio.viewmodel.StationsViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.action
import tornadofx.addClass
import tornadofx.booleanBinding
import tornadofx.controlsfx.glyph
import tornadofx.get
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.paddingTop
import tornadofx.stringBinding
import tornadofx.text
import tornadofx.vbox

/**
 * This is a view that shows different errors or info messages on stationsView
 */
class StationsEmptyView : BaseView() {

    private val viewModel: StationsViewModel by inject()

    private val searchGlyph by lazy { FontAwesome.Glyph.SEARCH.make(size = 50.0) }
    private val errorGlyph by lazy { FontAwesome.Glyph.SEARCH.make(size = 50.0) }
    private val noResultsGlyph by lazy { FontAwesome.Glyph.TIMES.make(size = 50.0) }

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
            paddingTop = 5.0
            id = "stationMessageSubHeader"
            addClass(Styles.grayLabel)
        }
    }

    private val connectionHelpMessage by lazy {
        hyperlink(messages["connectionErrorDesc"]) {
            action { Modal.Servers.open() }
            paddingTop = 5.0
            id = "stationMessageConnectionHelpMsg"
            addClass(Styles.grayLabel)
        }
    }

    private val graphic by lazy {
        glyph {
            viewModel.stateObservableChanges
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

        vbox(alignment = Pos.CENTER) {
            paddingTop = 10.0
            add(connectionHelpMessage)
            showWhen {
                viewModel.stateProperty.booleanBinding {
                    when (it) {
                        is StationsState.Error -> true
                        else -> false
                    }
                }
            }
        }

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
