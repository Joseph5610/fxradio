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

package online.hudacek.fxradio.ui.view.library

import javafx.beans.property.BooleanProperty
import javafx.scene.layout.Priority
import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.style.Colors
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class LibraryTitleFragment(title: String, showProperty: BooleanProperty, op: () -> Unit) : Fragment() {

    private val chevronStyleProperty = showProperty.objectBinding {
        if (it!!)
            FontAwesome.Glyph.CHEVRON_DOWN.make(size = 11.0,
                    useStyle = false,
                    color = c(Colors.values.grayLabel))
        else
            FontAwesome.Glyph.CHEVRON_RIGHT.make(size = 11.0,
                    useStyle = false,
                    color = c(Colors.values.grayLabel))
    }

    override val root = hbox {
        smallLabel(title) {
            paddingLeft = 10.0
        }
        region { hgrow = Priority.ALWAYS }
        smallLabel {
            graphicProperty().bind(chevronStyleProperty)
            paddingLeft = 10.0
            paddingRight = 10.0

            setOnMouseClicked {
                op()
            }

            showWhen {
                this@hbox.hoverProperty()
            }
        }
    }
}