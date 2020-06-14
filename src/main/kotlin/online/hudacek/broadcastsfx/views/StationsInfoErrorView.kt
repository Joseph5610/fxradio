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
package online.hudacek.broadcastsfx.views

import javafx.geometry.Pos
import online.hudacek.broadcastsfx.extension.glyph
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

/**
 * This is a view that shows different errors or info messages on stationsView
 */
class StationsInfoErrorView : View() {

    private val searchGlyph = glyph(FontAwesome.Glyph.SEARCH)
    private val errorGlyph = glyph(FontAwesome.Glyph.WARNING)
    private val loadingGlyph = glyph(FontAwesome.Glyph.SPINNER)

    private val header = label {
        addClass(Styles.header)
    }

    private val subHeader = label {
        addClass(Styles.grayLabel)
    }

    override val root = vbox(alignment = Pos.CENTER) {
        paddingTop = 120.0
        paddingLeft = 10.0
        paddingRight = 10.0
        add(header)
        add(subHeader)
    }

    fun showShortSearchInfo() {
        root.show()
        header.text = messages["searchingLibrary"]
        header.graphic = searchGlyph
        subHeader.text = messages["searchingLibraryDesc"]
    }

    fun showNoResultsInfo(query: String?) {
        root.show()
        if (query != null) {
            subHeader.text = messages["noResultsDesc"]
        }

        header.graphic = null
        header.text =
                if (query != null)
                    "${messages["noResultsFor"]} \"$query\""
                else {
                    messages["noResults"]
                }
    }

    fun showError() {
        root.show()
        header.graphic = errorGlyph
        header.text = messages["connectionError"]
        subHeader.text = messages["connectionErrorDesc"]
    }

    fun showLoading() {
        root.show()
        header.graphic = loadingGlyph
        header.text = messages["library.loading"]
        subHeader.text = ""
    }

    fun hide() = root.hide()
}
