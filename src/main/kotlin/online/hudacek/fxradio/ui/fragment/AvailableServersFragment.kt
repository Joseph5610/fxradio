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

package online.hudacek.fxradio.ui.fragment

import griffon.javafx.support.flagicons.FlagIcon
import javafx.geometry.Pos
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.ServersViewModel
import online.hudacek.fxradio.ui.viewmodel.ServersViewState
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import online.hudacek.fxradio.utils.showWhen
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

/**
 * Fragment that shows UI for selection of radio-browser API server
 * User is allowed to select a different server from the list.
 *
 * Selected server is then saved into the app.config property file
 * and used on the next start of the app
 */
class AvailableServersFragment : Fragment() {

    private val viewModel: ServersViewModel by inject()

    private val serverSavedProperty = Property(Properties.API_SERVER)

    private val labelTextProperty = viewModel.viewStateProperty.stringBinding {
        when (it) {
            ServersViewState.Loading -> {
                messages["loading"]
            }
            ServersViewState.Error -> {
                messages["servers.notAvailable"]
            }
            else -> {
                ""
            }
        }
    }

    override fun onDock() {
        viewModel.loadAvailableServers()
    }

    override val root = vbox {
        title = messages["menu.app.server"]
        paddingAll = 10.0
        setPrefSize(300.0, 230.0)

        vbox {
            vbox(alignment = Pos.CENTER) {
                label(messages["servers.title"]) {
                    paddingBottom = 15.0
                    addClass(Styles.header)
                }
            }

            vbox {
                prefHeight = 120.0
                alignment = Pos.BASELINE_CENTER
                label(labelTextProperty) {
                    paddingAll = 15.0
                }
                showWhen {
                    viewModel.viewStateProperty.isNotEqualTo(ServersViewState.Normal)
                }
            }

            listview(viewModel.serversProperty) {
                val savedServerValue: String? = serverSavedProperty.get()
                cellFormat {
                    graphic = hbox(5) {
                        prefHeight = 19.0
                        alignment = Pos.CENTER_LEFT

                        imageview {
                            image = FlagIcon(it.substring(0, 2))
                        }

                        label(it)

                        if (savedServerValue == it) {
                            label(messages["servers.selected"]) {
                                addClass(Styles.libraryListItemTag)
                            }
                        }
                    }
                    addClass(Styles.libraryListItem)
                }
                onUserSelect {
                    saveSelectedServer(it)
                }

                showWhen {
                    viewModel.viewStateProperty.isEqualTo(ServersViewState.Normal)
                }

                addClass(Styles.libraryListView)
            }
        }

        hbox(10) {
            alignment = Pos.CENTER_RIGHT
            paddingAll = 10.0

            button(messages["servers.reload"]) {
                action {
                    viewModel.loadAvailableServers(forceReload = true)
                }
            }
            button(messages["close"]) {
                isCancelButton = true
                action {
                    close()
                }
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }

    //Save the server in the app.config property file
    //Close the fragment after successful save
    private fun saveSelectedServer(server: String) = runAsync(daemon = true) {
        serverSavedProperty.save(server)
    } success {
        fire(NotificationEvent(messages["server.save.ok"], FontAwesome.Glyph.CHECK))
        close()
    } fail {
        fire(NotificationEvent(messages["server.save.error"]))
    }
}