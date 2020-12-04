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

package online.hudacek.fxradio.fragments

import griffon.javafx.support.flagicons.FlagIcon
import javafx.geometry.Pos
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.Properties
import online.hudacek.fxradio.Property
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.viewmodel.ServersViewModel
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

    override fun onDock() {
        runAsync {
            //In some cases, the list of servers might not be loaded when opening this fragment
            //This ensures the list is loaded at all times
            viewModel.loadAllServers()
        }
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
                addClass(Styles.libraryListView)
            }
        }

        hbox(10) {
            alignment = Pos.CENTER_RIGHT
            paddingAll = 10.0

            button(messages["close"]) {
                isCancelButton = true
                action {
                    close()
                }
            }
        }
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