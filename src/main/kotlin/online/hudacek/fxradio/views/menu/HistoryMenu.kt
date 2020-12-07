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

import online.hudacek.fxradio.storage.Database
import online.hudacek.fxradio.utils.menu
import online.hudacek.fxradio.utils.stationImage
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*

//History Menu
class HistoryMenu : Controller() {

    private val menuViewModel: MenuViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val historyViewModel: HistoryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    init {
        Database.history.select().subscribe({
            historyViewModel.item = HistoryModel(it.asObservable())
        }, {
            it.printStackTrace()
            historyViewModel.item = HistoryModel()
        })
    }

    val menu by lazy {
        menu(messages["menu.history"]) {
            item(messages["menu.history.show"]).action {
                libraryViewModel.selectedProperty.value = SelectedLibrary(LibraryType.History)
            }
            separator()
            menu(messages["menu.history.recent"]) {
                disableWhen {
                    historyViewModel.stationsProperty.emptyProperty()
                }
                items.bind(historyViewModel.stationsProperty) {
                    item("${it.name} (${it.countrycode})") {
                        //for some reason macos native menu does not respect
                        //width/height setting so it is disabled for now
                        if (!menuViewModel.useNative) {
                            graphic = imageview {
                                it.stationImage(this)
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
                    historyViewModel.stationsProperty.emptyProperty()
                }
                action {
                    confirm(messages["history.clear.confirm"], messages["history.clear.text"], owner = primaryStage) {
                        historyViewModel.cleanup()
                        libraryViewModel.refreshLibrary(LibraryType.History)
                    }
                }
            }
        }
    }
}
