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
import online.hudacek.fxradio.ui.util.FlagIcon
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.ui.util.smallLabel
import online.hudacek.fxradio.viewmodel.PreferencesViewModel
import online.hudacek.fxradio.viewmodel.ServersState
import online.hudacek.fxradio.viewmodel.ServersViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.action
import tornadofx.addClass
import tornadofx.bind
import tornadofx.bindSelected
import tornadofx.booleanBinding
import tornadofx.c
import tornadofx.checkbox
import tornadofx.combobox
import tornadofx.disableWhen
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.get
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.paddingAll
import tornadofx.radiobutton
import tornadofx.style
import tornadofx.togglegroup
import tornadofx.tooltip
import tornadofx.vbox

private const val GLYPH_SIZE = 13.0

/**
 * Change app preferences
 */
class PreferencesFragment : BaseFragment() {

    private val preferencesViewModel: PreferencesViewModel by inject()
    private val serversViewModel: ServersViewModel by inject()

    override fun onDock() = serversViewModel.fetchServers()

    override fun onUndock() {
        serversViewModel.commit()
    }

    override val root = vbox {
        title = messages["app.preferences"]
        paddingAll = 5.0

        form {
            fieldset(messages["app.appearance"]) {
                icon = FontAwesome.Glyph.PAINT_BRUSH.make(GLYPH_SIZE, isPrimary = false)
                field(messages["app.accentColor"]) {
                    labelContainer.alignment = Pos.CENTER_RIGHT
                    togglegroup {
                        AccentColor.values().forEach {
                            radiobutton(text = "", value = it, group = this) {
                                userData = it
                                style {
                                    baseColor = c(it.convertToHex())
                                }
                                action {
                                    preferencesViewModel.commit()
                                }
                                tooltip(it.humanName)
                                addClass(Styles.colorRadioButton)
                            }
                        }
                        bind(preferencesViewModel.accentColorProperty)

                        disableWhen(preferencesViewModel.useSystemColorProperty)
                    }
                }
                field(messages["app.useSystemColor"]) {
                    labelContainer.alignment = Pos.CENTER_RIGHT
                    checkbox {
                        bind(preferencesViewModel.useSystemColorProperty)
                        action {
                            preferencesViewModel.commit()
                        }
                    }
                }
            }

            fieldset(messages["app.darkMode"]) {
                icon = FontAwesome.Glyph.MOON_ALT.make(GLYPH_SIZE, isPrimary = false)

                field(messages["menu.app.darkmode"]) {
                    labelContainer.alignment = Pos.CENTER_RIGHT
                    togglegroup {
                        radiobutton(messages["menu.app.light"], value = false, group = this) {
                            action {
                                preferencesViewModel.commit()
                            }
                            addClass(Styles.colorRadioButton)
                        }
                        radiobutton(messages["menu.app.dark"], value = true, group = this) {
                            action {
                                preferencesViewModel.commit()
                            }
                            addClass(Styles.colorRadioButton)
                        }
                        bind(preferencesViewModel.darkModeProperty)
                    }
                }
            }

            fieldset(messages["app.trayIcon"]) {
                icon = FontAwesome.Glyph.SQUARE_ALT.make(GLYPH_SIZE, isPrimary = false)

                field(messages["app.trayIcon.enable"]) {
                    labelContainer.alignment = Pos.CENTER_RIGHT
                    checkbox {
                        bind(preferencesViewModel.useTrayIconProperty)
                        action {
                            preferencesViewModel.commit()
                        }
                    }
                }
            }

            fieldset(messages["menu.app.server"]) {
                icon = FontAwesome.Glyph.SERVER.make(GLYPH_SIZE, isPrimary = false)

                field(messages["servers.selected"]) {
                    labelContainer.alignment = Pos.CENTER_RIGHT
                    combobox(values = serversViewModel.availableServersProperty) {
                        cellFormat {
                            graphic = imageview { image = FlagIcon(it.substring(0, 2)).get() }
                            text = it
                        }
                        // Workaround for a strange bug...
                        selectionModel.clearSelection()
                        selectionModel.select(serversViewModel.selectedProperty.value)
                        bindSelected(serversViewModel.selectedProperty)
                    }

                    showWhen {
                        serversViewModel.stateProperty.booleanBinding {
                            when (it) {
                                is ServersState.Fetched -> true
                                else -> false
                            }
                        }
                    }
                }

                vbox(alignment = Pos.CENTER) {
                    hyperlink(messages["servers.notAvailable"]) {
                        addClass(Styles.grayLabel)
                        action {
                            serversViewModel.fetchServers()
                        }
                        showWhen {
                            serversViewModel.stateProperty.booleanBinding {
                                when (it) {
                                    is ServersState.Fetched -> false
                                    else -> true
                                }
                            }
                        }
                    }

                    smallLabel(messages["servers.restartNeeded"]) {
                        paddingAll = 5.0
                    }
                }
            }
        }
        addClass(Styles.backgroundWhite)
    }
}
