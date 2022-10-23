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

package online.hudacek.fxradio.ui.view.library

import com.github.thomasnield.rxkotlinfx.toObservable
import javafx.beans.property.BooleanProperty
import javafx.scene.layout.Priority
import javafx.util.Duration
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.paddingLeft
import tornadofx.paddingRight
import tornadofx.point
import tornadofx.region
import tornadofx.transform

private const val ICON_SIZE = 11.0

/**
 * Custom title fragment with hide/unhide icons
 */
class LibraryTitleFragment(title: String, showProperty: BooleanProperty, op: () -> Unit) : BaseFragment() {

    override val root = hbox {
        smallLabel(title) {
            paddingLeft = 10.0
        }
        region { hgrow = Priority.ALWAYS }
        smallLabel {
            paddingLeft = 10.0
            paddingRight = 10.0

            setOnMouseClicked {
                op()
            }

            graphic = FontAwesome.Glyph.CHEVRON_DOWN.make(size = ICON_SIZE, isPrimary = false)

            showProperty
                .toObservable()
                .subscribe {
                    if (it)
                        graphic.transform(
                            Duration.seconds(0.2), point(0.0, 0.0),
                            angle = 0.0,
                            scale = point(1.0, 1.0),
                            opacity = 1.0
                        )
                    else {
                        graphic.transform(
                            Duration.seconds(0.2), point(0.0, 0.0),
                            angle = -90.0,
                            scale = point(1.0, 1.0),
                            opacity = 1.0
                        )
                    }
                }

            showWhen {
                this@hbox.hoverProperty()
            }
        }
    }
}
