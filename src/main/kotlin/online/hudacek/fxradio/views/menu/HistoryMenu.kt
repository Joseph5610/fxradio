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

package online.hudacek.fxradio.views.menu

import online.hudacek.fxradio.viewmodel.LibraryType
import online.hudacek.fxradio.storage.Database
import online.hudacek.fxradio.utils.createImage
import online.hudacek.fxradio.utils.menu
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*

//History Menu
class HistoryMenu : Component(), ScopedInstance {

    private val menuViewModel: MenuViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val stationsHistoryViewModel: StationsHistoryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    init {
        Database.History.get().subscribe({
            println(it)
            stationsHistoryViewModel.item = StationsHistoryModel(it.asObservable())
        }, {
            it.printStackTrace()
            stationsHistoryViewModel.item = StationsHistoryModel()
        })
    }

    val menu by lazy {
        menu(messages["menu.history"]) {
            item(messages["menu.history.show"]).action {
                libraryViewModel.select(SelectedLibrary(LibraryType.History))
            }
            separator()
            menu(messages["menu.history.recent"]) {
                disableWhen {
                    stationsHistoryViewModel.stationsProperty.emptyProperty()
                }
                items.bind(stationsHistoryViewModel.stationsProperty) {
                    item("${it.name} (${it.countrycode})") {
                        //for some reason macos native menu does not respect
                        //width/height setting so it is disabled for now
                        if (!menuViewModel.useNative) {
                            graphic = imageview {
                                createImage(it)
                                fitHeight = 15.0
                                fitWidth = 15.0
                                isPreserveRatio = true
                            }
                        }
                        action {
                            playerViewModel.stationProperty.value = it
                        }
                    }
                }
            }
            separator()
            item(messages["menu.history.clear"]) {
                disableWhen {
                    stationsHistoryViewModel.stationsProperty.emptyProperty()
                }
                action {
                    confirm(messages["history.clear.confirm"], messages["history.clear.text"], owner = primaryStage) {
                        stationsHistoryViewModel.cleanup()
                        libraryViewModel.refreshLibrary(LibraryType.History)
                    }
                }
            }
        }
    }
}
