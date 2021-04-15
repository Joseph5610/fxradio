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

import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.ui.menu
import online.hudacek.fxradio.viewmodel.FavouritesViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class FavouritesMenu : BaseMenu() {

    private val playerViewModel: PlayerViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val playedStationNotInFavouritesProperty = playerViewModel.stationProperty.booleanBinding {
        //User should be able to add favourite station only when it is not already present
        it != null && !favouritesViewModel.stationsProperty.contains(it)
    }

    private val favouriteMenuItemVisibleProperty = playerViewModel.stationProperty.booleanBinding {
        it != null && it.isValid()
    }

    val addRemoveItems
        get() =
            listOf(
                    MenuItem(messages["menu.station.favourite"]).apply {
                        accelerator = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)
                        enableWhen(playedStationNotInFavouritesProperty)
                        visibleWhen(favouriteMenuItemVisibleProperty)

                        action {
                            appEvent.addFavourite.onNext(playerViewModel.stationProperty.value)
                            appEvent.refreshLibrary.onNext(LibraryState.Favourites)
                            playedStationNotInFavouritesProperty.invalidate()
                        }
                    },
                    //Remove favourite
                    MenuItem(messages["menu.station.favouriteRemove"]).apply {
                        disableWhen(playedStationNotInFavouritesProperty)
                        visibleWhen(favouriteMenuItemVisibleProperty)

                        action {
                            appEvent.removeFavourite.onNext(playerViewModel.stationProperty.value)
                            appEvent.refreshLibrary.onNext(LibraryState.Favourites)
                            playedStationNotInFavouritesProperty.invalidate()
                        }
                    }
            )

    init {
        Tables.favourites
                .selectAll()
                .subscribe {
                    favouritesViewModel.stationsProperty.add(it)
                }
    }

    override val menu by lazy {
        menu(messages["menu.favourites"]) {
            item(messages["menu.favourites.show"]).action {
                libraryViewModel.stateProperty.value = LibraryState.Favourites
            }

            items.addAll(addRemoveItems)

            //Clean all favourites
            separator()
            item(messages["menu.station.favourite.clear"]) {
                disableWhen {
                    favouritesViewModel.stationsProperty.emptyProperty()
                }
                action {
                    confirm(messages["database.clear.confirm"], messages["database.clear.text"], owner = primaryStage) {
                        appEvent.cleanupFavourites.onNext(Unit)
                        appEvent.refreshLibrary.onNext(LibraryState.Favourites)
                    }
                }
            }
        }
    }
}
