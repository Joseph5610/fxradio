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

import com.github.thomasnield.rxkotlinfx.actionEvents
import online.hudacek.fxradio.ui.util.stationView
import online.hudacek.fxradio.util.AlertHelper.confirmAlert
import online.hudacek.fxradio.viewmodel.FavouritesViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SelectedStation
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.action
import tornadofx.bind
import tornadofx.disableWhen
import tornadofx.get
import tornadofx.item

class FavouritesMenu : BaseMenu("menu.favourites") {

    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    override val menuItems = listOf(
        item(messages["menu.favourites.show"], KeyCodes.favouriteView) {
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
                    // For some reason macOS native menu does not respect
                    // width/height setting, so it is disabled for now
                    if (!appMenuViewModel.usePlatformProperty.value) {
                        graphic = stationView(it, 15.0)
                    }
                    action {
                        selectedStationViewModel.item = SelectedStation(it)
                    }
                }
            }
        },
        separator(),
        item(messages["menu.station.favourite.clear"]) {
            disableWhen {
                favouritesViewModel.stationsProperty.emptyProperty()
            }

            actionEvents()
                .flatMapMaybe { confirmAlert(messages["database.clear.confirm"], messages["database.clear.text"]) }
                .subscribe {
                    favouritesViewModel.cleanupFavourites()
                }
        }
    )
}
