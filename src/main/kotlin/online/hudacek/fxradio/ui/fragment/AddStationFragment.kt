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

package online.hudacek.fxradio.ui.fragment

import javafx.geometry.Pos
import online.hudacek.fxradio.apiclient.ApiUtils
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.field
import online.hudacek.fxradio.ui.util.requestFocusOnSceneAvailable
import online.hudacek.fxradio.util.actionEvents
import online.hudacek.fxradio.viewmodel.AddStationModel
import online.hudacek.fxradio.viewmodel.AddStationViewModel
import online.hudacek.fxradio.viewmodel.FavouritesViewModel
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.bind
import tornadofx.button
import tornadofx.checkbox
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane
import tornadofx.enableWhen
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.get
import tornadofx.hbox
import tornadofx.listProperty
import tornadofx.observableListOf
import tornadofx.validator
import java.util.Locale

class AddStationFragment : BaseFragment() {

    private val viewModel: AddStationViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()

    // List of Countries for autocomplete
    private val countriesListProperty = listProperty<String>(observableListOf()).apply {
        bind(libraryViewModel.countriesProperty) { c -> c.name }
    }

    // List of Languages for autocomplete
    private val languagesProperty = listProperty<String>(observableListOf()).apply {
        bind(libraryViewModel.countriesProperty) { c ->
            Locale.of(c.iso3166).displayLanguage
        }
    }

    override fun onDock() {
        // Recheck ViewModel validity when reopening fragment
        viewModel.validate(focusFirstError = false)
    }

    override val root = notificationPane(showFromTop = true) {
        title = messages["add.title"]
        prefWidth = 400.0

        content {
            form {
                fieldset {
                    requestFocusOnSceneAvailable()

                    field(
                        messages["add.name"], messages["add.station.prompt"],
                        viewModel.nameProperty, isRequired = true
                    ) { field ->
                        field.validator {
                            if (!validate(it, 400)) error(messages["field.invalid.length"])
                            else null
                        }
                    }

                    field(
                        messages["add.url"], "https://example.com/stream.m3u",
                        viewModel.urlProperty, isRequired = true
                    ) { field ->
                        field.validator {
                            if (it == null || !ApiUtils.isValidUrl(it)) {
                                error(messages["field.invalid.url"])
                            } else {
                                null
                            }
                        }
                    }

                    field(
                        messages["add.site"], "https://example.com/",
                        viewModel.homePageProperty, isRequired = false
                    ) { field ->
                        field.validator {
                            if (it == null || !ApiUtils.isValidUrl(it)) {
                                error(messages["field.invalid.url"])
                            } else {
                                null
                            }
                        }
                    }

                    field(
                        messages["add.icon"], "https://example.com/favicon.ico",
                        viewModel.faviconProperty, isRequired = false
                    ) { field ->
                        field.validator {
                            if (it == null || !ApiUtils.isValidUrl(it)) {
                                error(messages["field.invalid.url"])
                            } else {
                                null
                            }
                        }
                    }

                    field(
                        messages["add.language"], messages["add.language.prompt"],
                        viewModel.languageProperty, isRequired = true, languagesProperty
                    ) { field ->
                        field.validator {
                            if (!validate(it, 150)) error(messages["field.invalid.length"])
                            else null
                        }
                    }

                    field(
                        messages["add.country"], messages["add.country.prompt"],
                        viewModel.countryProperty, isRequired = true, countriesListProperty
                    ) { field ->
                        field.validator {
                            if (it !in countriesListProperty)
                                error(messages["field.invalid.country"])
                            else
                                null
                        }
                    }
                    field(messages["add.tags"], messages["add.tags.prompt"], viewModel.tagsProperty)
                    field {
                        checkbox(messages["add.favourites"], viewModel.saveToFavouritesProperty)
                    }
                }

                hbox(spacing = 5, alignment = Pos.CENTER_RIGHT) {
                    button(messages["cancel"]) {
                        isCancelButton = true
                        action {
                            close()
                        }
                    }
                    button(messages["save"]) {
                        enableWhen(viewModel.valid)
                        isDefaultButton = true

                        actionEvents()
                            .flatMapMaybe { viewModel.addNewStation() }
                            .subscribe(::save)

                        addClass(Styles.primaryButton)
                    }
                }
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }

    private fun validate(property: String?, maxValue: Int) = property?.length in 0 until maxValue

    private fun save(newStation: Station) {
        viewModel.commit {
            viewModel.saveToFavouritesObservable
                .filter { it }
                .map { newStation }
                .subscribe(favouritesViewModel::addFavourite)
        }

        // Cleanup view model
        viewModel.item = AddStationModel()

        close()
    }
}
