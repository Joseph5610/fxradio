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

import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.scene.layout.Priority
import online.hudacek.fxradio.apiclient.ApiUtils
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.field
import online.hudacek.fxradio.ui.set
import online.hudacek.fxradio.ui.stylableNotificationPane
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.AddStationModel
import online.hudacek.fxradio.viewmodel.AddStationViewModel
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.content

class AddStationFragment : BaseFragment() {

    private val viewModel: AddStationViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    //Autocomplete list of countries
    private val countriesListProperty = listProperty(observableListOf<String>())

    init {
        countriesListProperty.bind(libraryViewModel.countriesProperty) { it.name }
    }

    override fun onDock() {
        //Recheck viewmodel validity when reopening fragment
        viewModel.validate(focusFirstError = false)
    }

    override val root = stylableNotificationPane {
        title = messages["add.title"]
        prefWidth = 400.0

        content {
            form {
                fieldset(messages["add.title"]) {
                    vbox {
                        prefHeight = 50.0
                        vgrow = Priority.ALWAYS
                        label(messages["add.label"]) {
                            isWrapText = true
                        }
                    }

                    field(messages["add.name"], "My Radio Station",
                            viewModel.nameProperty, true) { field ->
                        field.validator {
                            if (!validate(it, 400)) error(messages["field.invalid.length"])
                            else null
                        }
                    }

                    field(messages["add.site"], "https://example.com/",
                            viewModel.homePageProperty) { field ->
                        field.validator {
                            if (it == null || !ApiUtils.isValidUrl(it)) {
                                error(messages["field.invalid.url"])
                            } else {
                                null
                            }
                        }
                    }

                    field(messages["add.url"], "https://example.com/stream.m3u",
                            viewModel.urlProperty) { field ->
                        field.validator {
                            if (it == null || !ApiUtils.isValidUrl(it)) {
                                error(messages["field.invalid.url"])
                            } else {
                                null
                            }
                        }
                    }

                    field(messages["add.icon"], "https://example.com/favicon.ico",
                            viewModel.faviconProperty) { field ->
                        field.validator {
                            if (it == null || !ApiUtils.isValidUrl(it)) {
                                error(messages["field.invalid.url"])
                            } else {
                                null
                            }
                        }
                    }

                    field(messages["add.language"], messages["add.language.prompt"],
                            viewModel.languageProperty, true) { field ->
                        field.validator {
                            if (!validate(it, 150)) error(messages["field.invalid.length"])
                            else null
                        }
                    }

                    field(messages["add.country"], messages["add.country.prompt"],
                            viewModel.countryProperty, true, countriesListProperty) { field ->
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

                hbox(5) {
                    button(messages["save"]) {
                        enableWhen(viewModel.valid)
                        isDefaultButton = true

                        actionEvents()
                                .flatMapSingle { viewModel.addStation() }
                                .subscribe {
                                    if (it.ok) {
                                        //Save UUID of new station
                                        viewModel.uuidProperty.value = it.uuid

                                        viewModel.commit {
                                            close()

                                            //Cleanup view model
                                            viewModel.item = AddStationModel()
                                        }
                                    } else {
                                        this@stylableNotificationPane[FontAwesome.Glyph.WARNING] = it.message
                                    }
                                }
                        addClass(Styles.primaryButton)
                    }

                    button(messages["add.cleanupForm"]) {
                        action {
                            viewModel.item = AddStationModel()
                        }
                    }

                    button(messages["cancel"]) {
                        isCancelButton = true
                        action {
                            close()
                        }
                    }
                }
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }

    private fun validate(property: String?, maxValue: Int) = property?.length in 0 until maxValue
}