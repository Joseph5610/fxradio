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

package online.hudacek.fxradio.ui.view.menu

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import mu.KotlinLogging
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.storage.stationImage
import online.hudacek.fxradio.ui.viewmodel.*
import online.hudacek.fxradio.utils.menu
import tornadofx.*

private val logger = KotlinLogging.logger {}

class HistoryMenu : FxMenu() {

    private val libraryViewModel: LibraryViewModel by inject()
    private val historyViewModel: HistoryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    private val keyHistory = KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN)

    init {
        Tables.history
                .select()
                .subscribe({
                    historyViewModel.item = HistoryModel(it.asObservable())
                }, {
                    logger.error(it) { "Error while getting history from DB!" }
                })
    }

    override val menu by lazy {
        menu(messages["menu.history"]) {
            item(messages["menu.history.show"], keyHistory).action {
                libraryViewModel.selectedProperty.value = SelectedLibrary(LibraryType.History)
            }

            menu(messages["menu.history.recent"]) {
                disableWhen {
                    historyViewModel.stationsProperty.emptyProperty()
                }
                items.bind(historyViewModel.stationsProperty) {
                    item("${it.name} (${it.countrycode})") {
                        //for some reason macos native menu does not respect
                        //width/height setting so it is disabled for now
                        if (!menuViewModel.usePlatformProperty.value) {
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
                        historyViewModel.cleanupHistory.onNext(Unit)
                        libraryViewModel.refreshLibrary.onNext(LibraryType.History)
                    }
                }
            }
        }
    }
}
