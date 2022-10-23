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
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.style.AccentColor
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.AppAppearanceViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.bind
import tornadofx.button
import tornadofx.c
import tornadofx.checkbox
import tornadofx.disableWhen
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.get
import tornadofx.hbox
import tornadofx.paddingAll
import tornadofx.radiobutton
import tornadofx.style
import tornadofx.togglegroup
import tornadofx.tooltip
import tornadofx.vbox

/**
 * Set app appearance
 */
class PreferencesFragment : BaseFragment() {

    private val appAppearanceViewModel: AppAppearanceViewModel by inject()

    override val root = vbox {
        paddingAll = 5.0
        title = messages["app.preferences"]

        form {
            fieldset(messages["app.appearance"]) {
                field(messages["app.accentColor"]) {
                    togglegroup {
                        AccentColor.values().forEach {
                            radiobutton(text = "", value = it, group = this) {
                                userData = it
                                style {
                                    baseColor = c(it.convertToHex())
                                }
                                action {
                                    appAppearanceViewModel.commit()
                                }
                                tooltip(it.humanName)
                            }
                        }
                        bind(appAppearanceViewModel.accentColorProperty)

                        disableWhen(appAppearanceViewModel.useSystemColorProperty)
                    }
                }
                field(messages["app.useSystemColor"]) {
                    checkbox {
                        bind(appAppearanceViewModel.useSystemColorProperty)
                        action {
                            appAppearanceViewModel.commit()
                        }
                    }
                }
            }

            fieldset(messages["app.darkMode"]) {
                field(messages["menu.app.darkmode"]) {
                    checkbox {
                        bind(appAppearanceViewModel.darkModeProperty)
                        action {
                            appAppearanceViewModel.commit()
                        }
                    }
                }
            }

            hbox(spacing = 5, alignment = Pos.CENTER_RIGHT) {
                button(messages["cancel"]) {
                    isCancelButton = true
                    action {
                        close()
                    }
                }
            }
        }

        addClass(Styles.backgroundWhite)
    }
}
