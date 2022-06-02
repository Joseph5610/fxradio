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

package online.hudacek.fxradio.ui.menu

import javafx.scene.control.MenuItem
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.viewmodel.FavouritesViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class FavouritesMenu : BaseMenu("menu.favourites") {

    private val playerViewModel: PlayerViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val playedStationNotInFavouritesProperty = playerViewModel.stationProperty.booleanBinding {
        //User should be able to add favourite station only when it is not already present
        it != null && it !in favouritesViewModel.stationsProperty
    }

    private val favouriteMenuItemVisibleProperty = playerViewModel.stationProperty.booleanBinding {
        it != null && it.isValid()
    }

    /**
     * Items for add/remove favourite station reused in multiple menus around the app
     */
    val addRemoveFavouriteItems
        get() =
            mutableListOf(
                    item(messages["menu.station.favourite"], KeyCodes.favourite) {
                        enableWhen(playedStationNotInFavouritesProperty)
                        visibleWhen(favouriteMenuItemVisibleProperty)

                        action {
                            appEvent.addFavourite.onNext(playerViewModel.stationProperty.value)
                            appEvent.refreshLibrary.onNext(LibraryState.Favourites)
                            playedStationNotInFavouritesProperty.invalidate()
                        }
                    },
                    //Remove favourite
                    item(messages["menu.station.favouriteRemove"]) {
                        disableWhen(playedStationNotInFavouritesProperty)
                        visibleWhen(favouriteMenuItemVisibleProperty)

                        action {
                            appEvent.removeFavourite.onNext(playerViewModel.stationProperty.value)
                            appEvent.refreshLibrary.onNext(LibraryState.Favourites)
                            playedStationNotInFavouritesProperty.invalidate()
                        }
                    }
            )

    override val menuItems = mutableListOf<MenuItem>().apply {
        addAll(listOf(
                item(messages["menu.favourites.show"]) {
                    action {
                        libraryViewModel.stateProperty.value = LibraryState.Favourites
                    }
                },
                menu(messages["menu.favourites.all"]) {
                    disableWhen {
                        favouritesViewModel.stationsProperty.emptyProperty()
                    }
                    items.bind(favouritesViewModel.stationsProperty) {
                        item(it.name) {
                            //for some reason macos native menu does not respect
                            //width/height setting so it is disabled for now
                            if (!appMenuViewModel.usePlatformProperty.value) {
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
                }))
        addAll(addRemoveFavouriteItems)
        addAll(listOf(separator(),
                item(messages["menu.station.favourite.clear"]) {
                    disableWhen {
                        favouritesViewModel.stationsProperty.emptyProperty()
                    }
                    action {
                        confirm(messages["database.clear.confirm"], messages["database.clear.text"], owner = primaryStage) {
                            favouritesViewModel.cleanupFavourites()
                            appEvent.refreshLibrary.onNext(LibraryState.Favourites)
                        }
                    }
                }))
    }
}
