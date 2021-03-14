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
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.ui.menu
import online.hudacek.fxradio.ui.viewmodel.*
import tornadofx.*

private val logger = KotlinLogging.logger {}

class FavouritesMenu : FxMenu() {
    private val appEvent: AppEvent by inject()

    private val selectedLibraryViewModel: SelectedLibraryViewModel by inject()

    private val playerViewModel: PlayerViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()

    private val keyFavourites = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)

    private val playedStationNotInFavouritesProperty = playerViewModel.stationProperty.booleanBinding {
        //User should be able to add favourite station only when it is not already present
        it != null && !favouritesViewModel.stationsProperty.contains(it)
    }

    private val favouriteMenuItemVisibleProperty = playerViewModel.stationProperty.booleanBinding {
        it != null && it.isValid()
    }

    init {
        Tables.favourites
                .selectAll()
                .subscribe({
                    favouritesViewModel.stationsProperty.add(it)
                }, {
                    logger.error(it) { "Error while getting favourite stations" }
                })
    }

    override val menu by lazy {
        menu(messages["menu.favourites"]) {
            item(messages["menu.favourites.show"]).action {
                selectedLibraryViewModel.item = SelectedLibrary(LibraryType.Favourites)
            }

            //Add favourite
            item(messages["menu.station.favourite"], keyFavourites) {
                enableWhen(playedStationNotInFavouritesProperty)
                visibleWhen(favouriteMenuItemVisibleProperty)

                action {
                    appEvent.addFavourite.onNext(playerViewModel.stationProperty.value)
                    appEvent.refreshLibrary.onNext(LibraryType.Favourites)
                    playedStationNotInFavouritesProperty.invalidate()
                }
            }

            //Remove favourite
            item(messages["menu.station.favouriteRemove"]) {
                disableWhen(playedStationNotInFavouritesProperty)
                visibleWhen(favouriteMenuItemVisibleProperty)

                action {
                    appEvent.removeFavourite.onNext(playerViewModel.stationProperty.value)
                    appEvent.refreshLibrary.onNext(LibraryType.Favourites)
                    playedStationNotInFavouritesProperty.invalidate()
                }
            }

            //Clean all favourites
            separator()
            item(messages["menu.station.favourite.clear"]) {
                disableWhen {
                    favouritesViewModel.stationsProperty.emptyProperty()
                }
                action {
                    confirm(messages["database.clear.confirm"], messages["database.clear.text"], owner = primaryStage) {
                        appEvent.cleanupFavourites.onNext(Unit)
                        appEvent.refreshLibrary.onNext(LibraryType.Favourites)
                    }
                }
            }
        }
    }
}
