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

package online.hudacek.fxradio.ui.menu

import online.hudacek.fxradio.ui.formatted
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.viewmodel.HistoryViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class HistoryMenu : BaseMenu("menu.history") {

    private val libraryViewModel: LibraryViewModel by inject()
    private val historyViewModel: HistoryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    private val showHistoryItem by lazy {
        item(messages["menu.history.show"], KeyCodes.history) {
            action {
                libraryViewModel.stateProperty.value = LibraryState.History
            }
        }
    }

    private val recentHistoryItem by lazy {
        menu(messages["menu.history.recent"]) {
            disableWhen {
                historyViewModel.stationsProperty.emptyProperty()
            }
            items.bind(historyViewModel.stationsProperty) {
                item(it.name) {
                    //for some reason macos native menu does not respect
                    //width/height setting so it is disabled for now
                    if (!appMenuViewModel.usePlatformProperty.value) {
                        graphic = imageview {
                            it.stationImage(this)
                            fitHeight = 15.0
                            fitWidth = 15.0
                        }
                    }
                    action {
                        playerViewModel.stationProperty.value = it
                    }
                }
            }
        }
    }

    private val clearHistoryItem by lazy {
        item(messages["menu.history.clear"]) {
            disableWhen {
                historyViewModel.stationsProperty.emptyProperty()
            }

            action {
                confirm(messages["history.clear.confirm"],
                        messages["history.clear.text"].formatted(historyViewModel.stationsProperty.size), owner = primaryStage) {
                    historyViewModel.cleanupHistory()
                }
            }
        }
    }

    override val menuItems = listOf(showHistoryItem, recentHistoryItem, separator(), clearHistoryItem)
}
