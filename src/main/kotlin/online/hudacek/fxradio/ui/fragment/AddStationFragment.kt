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

package online.hudacek.fxradio.ui.fragment

import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.scene.layout.Priority
import mu.KotlinLogging
import okhttp3.HttpUrl
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.AddStationBody
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.AddStationModel
import online.hudacek.fxradio.ui.viewmodel.AddStationViewModel
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.utils.applySchedulers
import online.hudacek.fxradio.utils.set
import online.hudacek.fxradio.utils.stylableNotificationPane
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.bindAutoCompletion
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

                    field(messages["add.name"]) {
                        textfield(viewModel.nameProperty) {
                            required()
                            validator {
                                if (validate(it, maxValue = 400))
                                    success()
                                else
                                    error(messages["field.invalid.length"])
                            }
                            promptText = "My Radio Station"
                        }
                    }

                    field(messages["add.site"]) {
                        textfield(viewModel.homePageProperty) {
                            required()
                            validator {
                                if (it != null && HttpUrl.parse(it) != null)
                                    success()
                                else
                                    error(messages["field.invalid.url"])

                            }
                            promptText = "https://example.com/"
                        }
                    }
                    field(messages["add.url"]) {
                        textfield(viewModel.urlProperty) {
                            required()
                            validator {
                                if (it != null && HttpUrl.parse(it) != null)
                                    success()
                                else
                                    error(messages["field.invalid.url"])
                            }
                            promptText = "https://example.com/stream.m3u"
                        }
                    }
                    field(messages["add.icon"]) {
                        textfield(viewModel.faviconProperty) {
                            required()
                            validator {
                                if (it != null && HttpUrl.parse(it) != null)
                                    success()
                                else
                                    error(messages["field.invalid.url"])
                            }
                            promptText = "https://example.com/favicon.ico"
                        }
                    }
                    field(messages["add.language"]) {
                        textfield(viewModel.languageProperty) {
                            required()
                            validator {
                                if (validate(it))
                                    success()
                                else
                                    error(messages["field.invalid.length"])
                            }
                            promptText = messages["add.language.prompt"]
                        }
                    }
                    field(messages["add.country"]) {
                        textfield(viewModel.countryProperty) {
                            bindAutoCompletion(viewModel.countriesListProperty)
                            required()

                            validator {
                                if (viewModel.countriesListProperty.contains(it))
                                    success()
                                else
                                    error(messages["field.invalid.country"])
                            }
                            promptText = messages["add.country.prompt"]
                        }
                    }
                    field(messages["add.tags"]) {
                        textfield(viewModel.tagsProperty) {
                            promptText = messages["add.tags.prompt"]
                        }
                    }
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
                                            .add(AddStationBody(
                                                    viewModel.nameProperty.value, viewModel.urlProperty.value,
                                                    viewModel.homePageProperty.value,
                                                    viewModel.faviconProperty.value, viewModel.countryCodeProperty.value,
                                                    viewModel.countryProperty.value, viewModel.languageProperty.value,
                                                    viewModel.tagsProperty.value
                                            ))
                                            .compose(applySchedulers())
                                }.subscribe({
                                    if (it.ok) {

                                        //Save UUID of new station
                                        viewModel.uuidProperty.value = it.uuid

                                        viewModel.commit {
                                            fire(NotificationEvent(messages["add.success"], FontAwesome.Glyph.CHECK))
                                            close()

                                            //Cleanup view model
                                            viewModel.item = AddStationModel()
                                        }

                                    } else {
                                        logger.error { "Error while adding station: ${it.message} " }
                                        this@stylableNotificationPane[FontAwesome.Glyph.WARNING] = it.message
                                    }
                                }, {
                                    logger.error(it) { "Error while adding station " }
                                    this@stylableNotificationPane[FontAwesome.Glyph.WARNING] = messages["add.error"]
                                })
                        addClass(Styles.primaryButton)
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

    private fun validate(property: String?, minValue: Int = 3, maxValue: Int = 150) =
            property?.length in (minValue + 1) until maxValue
}