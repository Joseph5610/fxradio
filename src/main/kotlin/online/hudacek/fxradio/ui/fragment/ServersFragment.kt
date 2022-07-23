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

import griffon.javafx.support.flagicons.FlagIcon
import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.ServersState
import online.hudacek.fxradio.viewmodel.ServersViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.action
import tornadofx.addClass
import tornadofx.bindSelected
import tornadofx.booleanBinding
import tornadofx.button
import tornadofx.enableWhen
import tornadofx.get
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.isDirty
import tornadofx.label
import tornadofx.listview
import tornadofx.paddingAll
import tornadofx.paddingBottom
import tornadofx.stringBinding
import tornadofx.text
import tornadofx.vbox

/**
 * Fragment that shows UI for selection of radio-browser API server
 * User is allowed to select a different server from the list.
 *
 * Selected server is then saved into the app.config property file
 * and used on the next start of the app
 */
class ServersFragment : BaseFragment() {

    private val viewModel: ServersViewModel by inject()

    private val labelTextProperty = viewModel.stateProperty.stringBinding {
        when (it) {
            is ServersState.Error -> {
                messages[it.key] + it.cause
            }
            else -> {
                messages[it?.key ?: ""]
            }
        }
    }

    override fun onDock() = viewModel.fetchServers()

    override fun onUndock() {
        if (viewModel.selectedProperty.isDirty) {
            viewModel.rollback(viewModel.selectedProperty)
        }
    }

    override val root = vbox {
        title = messages["menu.app.server"]
        paddingAll = 10.0
        setPrefSize(350.0, 250.0)

        vbox {
            vbox(alignment = Pos.CENTER) {
                label(messages["servers.title"]) {
                    paddingBottom = 15.0
                    addClass(Styles.header)
                }

                label(messages["servers.restartNeeded"]) {
                    paddingAll = 5.0
                    textAlignment = TextAlignment.CENTER
                }
            }

            vbox {
                prefHeight = 120.0
                alignment = Pos.BASELINE_CENTER
                text(labelTextProperty) {
                    paddingAll = 5.0
                    wrappingWidth = 270.0
                    textAlignment = TextAlignment.CENTER
                    addClass(Styles.defaultTextColor)
                }

                showWhen {
                    viewModel.stateProperty.booleanBinding {
                        when (it) {
                            is ServersState.Fetched -> false
                            else -> true
                        }
                    }
                }
            }

            listview(viewModel.serversProperty) {
                bindSelected(viewModel.selectedProperty)
                cellFormat {
                    graphic = hbox(5) {
                        prefHeight = 19.0
                        alignment = Pos.CENTER_LEFT

                        imageview {
                            image = runCatching { FlagIcon(it.substring(0, 2)) }.getOrNull()
                        }

                        label(messages["servers.selected"]) {
                            showWhen {
                                //look for the value of backing field
                                booleanBinding(viewModel.item.selected) {
                                    this == it
                                }
                            }
                            addClass(Styles.libraryListItemTag)
                        }
                    }
                    text = it
                    addClass(Styles.libraryListItem)
                }

                showWhen {
                    viewModel.stateProperty.booleanBinding {
                        when (it) {
                            is ServersState.Fetched -> true
                            else -> false
                        }
                    }
                }

                addClass(Styles.libraryListView)
            }
        }

        hbox(10) {
            alignment = Pos.CENTER_RIGHT
            paddingAll = 10.0

            button(messages["servers.reload"]) {
                action {
                    viewModel.fetchServers()
                }
            }
            button(messages["save"]) {
                enableWhen(viewModel.selectedProperty.isNotNull)
                isDefaultButton = true
                action {
                    //Save the server in the app.config property file
                    //Close the fragment after successful save
                    viewModel.commit {
                        appEvent.appNotification.onNext(AppNotification(messages["server.save.ok"], FontAwesome.Glyph.CHECK))
                        close()
                    }
                }
                addClass(Styles.primaryButton)
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}
