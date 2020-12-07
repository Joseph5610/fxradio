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

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.storage.Database
import online.hudacek.fxradio.utils.menu
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*

class FavouritesMenu : Controller() {

    private val libraryViewModel: LibraryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()

    private val keyFavourites = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)

    init {
        Database.favourites.select().subscribe({
            favouritesViewModel.item = FavouritesModel(it.asObservable())
        }, {
            it.printStackTrace()
            favouritesViewModel.item = FavouritesModel()
        })
    }

    val menu by lazy {
        menu(messages["menu.favourites"]) {
            item(messages["menu.favourites.show"]).action {
                libraryViewModel.selectedProperty.value = SelectedLibrary(LibraryType.Favourites)
            }
            separator()

            //Add favourite
            item(messages["menu.station.favourite"], keyFavourites) {
                enableWhen {
                    favouritesViewModel.stationsProperty.booleanBinding {
                        !it!!.contains(playerViewModel.stationProperty.value)
                    }.and(playerViewModel.stationProperty.booleanBinding {
                        it != null && it.isValid() && !favouritesViewModel.stationsProperty.contains(it)
                    })
                }

                action {
                    favouritesViewModel.add(playerViewModel.stationProperty.value)
                    libraryViewModel.refreshLibrary(LibraryType.Favourites)
                }
            }

            //Remove favourite
            item(messages["menu.station.favourite.remove"]) {
                enableWhen {
                    favouritesViewModel.stationsProperty.booleanBinding {
                        it!!.contains(playerViewModel.stationProperty.value)
                    }.and(playerViewModel.stationProperty.booleanBinding {
                        it != null && it.isValid() && favouritesViewModel.stationsProperty.contains(it)
                    })
                }
                action {
                    favouritesViewModel.remove(playerViewModel.stationProperty.value)
                    libraryViewModel.refreshLibrary(LibraryType.Favourites)
                }
            }

            //Clean all favourites
            separator()
            item(messages["menu.station.favourite.clear"]) {
                disableWhen {
                    favouritesViewModel.stationsProperty.emptyProperty()
                }
                action {
                    favouritesViewModel.cleanup()
                    libraryViewModel.refreshLibrary(LibraryType.Favourites)
                }
            }
        }
    }
}
