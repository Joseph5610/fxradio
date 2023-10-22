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

import javafx.beans.property.BooleanProperty
import javafx.scene.layout.Priority
import javafx.util.Duration
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.ui.util.smallLabel
import online.hudacek.fxradio.util.toObservable
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.onLeftClick
import tornadofx.paddingLeft
import tornadofx.paddingRight
import tornadofx.paddingTop
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
    private val showProperty: BooleanProperty? by param()

    private val showIcon: Glyph by lazy {
        FontAwesome.Glyph.CHEVRON_DOWN.make(size = ICON_SIZE, isPrimary = false) {
            paddingLeft = 10.0
            paddingRight = 10.0

            showProperty
                ?.toObservable()
                ?.subscribe {
                    if (it) {
                        transform(
                            time = duration,
                            destination = destination,
                            angle = 0.0,
                            scale = originalScale,
                            opacity = 1.0
                        )
                    } else {
                        transform(
                            time = duration,
                            destination = destination,
                            angle = -90.0,
                            scale = originalScale,
                            opacity = 1.0
                        )
                    }
                }
        }
    }

    override val root = hbox {
        paddingTop = 10.0

        smallLabel(libraryTitle) {
            paddingLeft = 10.0
        }
        if (showProperty != null) {
            onLeftClick {
                showProperty!!.value = !showProperty!!.value
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

    companion object {
        private val originalScale = point(1.0, 1.0)
        private val destination = point(0.0, 0.0)
        private val duration: Duration = Duration.seconds(0.2)
    }
}
