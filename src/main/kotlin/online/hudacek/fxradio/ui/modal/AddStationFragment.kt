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

package online.hudacek.fxradio.ui.modal

import com.github.thomasnield.rxkotlinfx.actionEvents
import io.reactivex.Single
import javafx.scene.layout.Priority
import mu.KotlinLogging
import okhttp3.HttpUrl
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.AddedStation
import online.hudacek.fxradio.ui.field
import online.hudacek.fxradio.ui.set
import online.hudacek.fxradio.ui.stylableNotificationPane
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.AddStation
import online.hudacek.fxradio.ui.viewmodel.AddStationViewModel
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.utils.applySchedulers
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.content

private val logger = KotlinLogging.logger {}

class AddStationFragment : Fragment() {
    private val viewModel: AddStationViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    init {
        //Bind autocomplete list of countries
        viewModel.countriesListProperty.bind(libraryViewModel.countriesProperty) { it.name }
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
                            if (it != null && HttpUrl.parse(it) != null)
                                success()
                            else
                                error(messages["field.invalid.url"])
                        }
                    }

                    field(messages["add.url"], "https://example.com/stream.m3u",
                            viewModel.urlProperty) { field ->
                        field.validator {
                            if (it != null && HttpUrl.parse(it) != null)
                                success()
                            else
                                error(messages["field.invalid.url"])
                        }
                    }

                    field(messages["add.icon"], "https://example.com/favicon.ico",
                            viewModel.faviconProperty) { field ->
                        field.validator {
                            if (it != null && HttpUrl.parse(it) != null)
                                success()
                            else
                                error(messages["field.invalid.url"])
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
                            viewModel.countryProperty, true, viewModel.countriesListProperty) { field ->
                        field.validator {
                            if (viewModel.countriesListProperty.contains(it))
                                success()
                            else
                                error(messages["field.invalid.country"])
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
                                .flatMapSingle {
                                    StationsApi.service
                                            .add(online.hudacek.fxradio.api.model.AddStation(
                                                    viewModel.nameProperty.value, viewModel.urlProperty.value,
                                                    viewModel.homePageProperty.value,
                                                    viewModel.faviconProperty.value, viewModel.countryCodeProperty.value,
                                                    viewModel.countryProperty.value, viewModel.languageProperty.value,
                                                    viewModel.tagsProperty.value
                                            ))
                                            .compose(applySchedulers())
                                            .onErrorResumeNext { Single.just(AddedStation(false, it.localizedMessage, "invalid")) }
                                }.subscribe {
                                    if (it.ok) {
                                        //Save UUID of new station
                                        viewModel.uuidProperty.value = it.uuid

                                        viewModel.commit {
                                            close()

                                            //Cleanup view model
                                            viewModel.item = AddStation()
                                        }
                                    } else {
                                        logger.error { "Error while adding station: ${it.message} " }
                                        this@stylableNotificationPane[FontAwesome.Glyph.WARNING] = it.message
                                    }
                                }
                        addClass(Styles.primaryButton)
                    }

                    button(messages["add.cleanupForm"]) {
                        action {
                            viewModel.item = AddStation()
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