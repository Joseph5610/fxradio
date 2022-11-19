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
import org.controlsfx.glyphfont.Glyph
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.onLeftClick
import tornadofx.paddingLeft
import tornadofx.paddingRight
import tornadofx.point
import tornadofx.region
import tornadofx.transform
import tornadofx.vbox

private const val ICON_SIZE = 11.0

/**
 * Custom title fragment with hide/unhide icons
 */
class LibraryTitleFragment : BaseFragment() {

    private val libraryTitle: String by param()
    private val showProperty: BooleanProperty by param()

    private val showIcon: Glyph by lazy {
        FontAwesome.Glyph.CHEVRON_DOWN.make(size = ICON_SIZE, isPrimary = false) {
            paddingLeft = 10.0
            paddingRight = 10.0

            onLeftClick {
                showProperty.value = !showProperty.value
            }

            showProperty
                .toObservable()
                .subscribe {
                    if (it)
                        transform(
                            Duration.seconds(0.2), point(0.0, 0.0),
                            angle = 0.0,
                            scale = point(1.0, 1.0),
                            opacity = 1.0
                        )
                    else {
                        transform(
                            Duration.seconds(0.2), point(0.0, 0.0),
                            angle = -90.0,
                            scale = point(1.0, 1.0),
                            opacity = 1.0
                        )
                    }
                }
        }
    }

    override val root = hbox {
        smallLabel(libraryTitle) {
            paddingLeft = 10.0
        }
        region { hgrow = Priority.ALWAYS }
        vbox {
            add(showIcon)
            showWhen {
                this@hbox.hoverProperty()
            }
        }
    }
}
