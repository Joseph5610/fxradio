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

import javafx.scene.layout.Priority
import okhttp3.HttpUrl
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.AddStationBody
import online.hudacek.fxradio.api.model.AddStationResponse
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.AddStationModel
import online.hudacek.fxradio.ui.viewmodel.AddStationViewModel
import online.hudacek.fxradio.ui.viewmodel.FavouritesViewModel
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.utils.applySchedulers
import online.hudacek.fxradio.utils.set
import online.hudacek.fxradio.utils.stylableNotificationPane
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.bindAutoCompletion
import tornadofx.controlsfx.content

class AddStationFragment : Fragment() {

    private val viewModel: AddStationViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()

    init {
        viewModel.autoCompleteCountriesProperty.bind(libraryViewModel.countriesProperty) { it.name }
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
                        textfield(viewModel.name) {
                            required()
                            validator {
                                if (validate(it, maxValue = 400))
                                    null
                                else
                                    error(messages["field.invalid.length"])
                            }
                            promptText = "My Radio Station"
                        }
                    }

                    field(messages["add.site"]) {
                        textfield(viewModel.homepage) {
                            required()
                            validator {
                                if (it != null && HttpUrl.parse(it) != null)
                                    null
                                else
                                    error(messages["field.invalid.url"])

                            }
                            promptText = "https://example.com/"
                        }
                    }
                    field(messages["add.url"]) {
                        textfield(viewModel.url) {
                            required()
                            validator {
                                if (it != null && HttpUrl.parse(it) != null)
                                    null
                                else
                                    error(messages["field.invalid.url"])
                            }
                            promptText = "https://example.com/stream.m3u"
                        }
                    }
                    field(messages["add.icon"]) {
                        textfield(viewModel.favicon) {
                            required()
                            validator {
                                if (it != null && HttpUrl.parse(it) != null)
                                    null
                                else
                                    error(messages["field.invalid.url"])
                            }
                            promptText = "https://example.com/favicon.ico"
                        }
                    }
                    field(messages["add.language"]) {
                        textfield(viewModel.language) {
                            required()
                            validator {
                                if (validate(it))
                                    null
                                else
                                    error(messages["field.invalid.length"])
                            }
                            promptText = messages["add.language.prompt"]
                        }
                    }
                    field(messages["add.country"]) {
                        textfield(viewModel.country) {
                            bindAutoCompletion(viewModel.autoCompleteCountriesProperty)
                            required()

                            validator {
                                if (viewModel.autoCompleteCountriesProperty.contains(it))
                                    null
                                else
                                    error(messages["field.invalid.country"])
                            }
                            promptText = messages["add.country.prompt"]
                        }
                    }
                    field(messages["add.tags"]) {
                        textfield(viewModel.tags) {
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
                        addClass(Styles.primaryButton)
                        action {
                            viewModel.commit {
                                StationsApi.service
                                        .add(AddStationBody(
                                                name = viewModel.name.value,
                                                url = viewModel.url.value,
                                                homepage = viewModel.homepage.value,
                                                favicon = viewModel.favicon.value,
                                                country = viewModel.country.value,
                                                countryCode = viewModel.countryCode.value,
                                                state = viewModel.state.value,
                                                language = viewModel.language.value,
                                                tags = viewModel.tags.value
                                        ))
                                        .compose(applySchedulers())
                                        .subscribe({
                                            saveToFavourites(it)
                                            fire(NotificationEvent(messages["add.success"], FontAwesome.Glyph.CHECK))
                                            close()
                                            //Cleanup view model
                                            viewModel.item = AddStationModel(AddStationBody())
                                        }, {
                                            this@stylableNotificationPane[FontAwesome.Glyph.WARNING] = messages["add.error"]
                                        })
                            }
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

    private fun validate(property: String?, minValue: Int = 3, maxValue: Int = 150) =
            property?.length in (minValue + 1) until maxValue

    private fun saveToFavourites(response: AddStationResponse) {
        if (viewModel.saveToFavouritesProperty.value) {
            val station = Station(
                    stationuuid = response.uuid,
                    name = viewModel.name.value,
                    url_resolved = viewModel.url.value,
                    homepage = viewModel.homepage.value,
                    favicon = viewModel.favicon.value,
                    country = viewModel.country.value,
                    countrycode = viewModel.countryCode.value,
                    state = viewModel.state.value,
                    language = viewModel.language.value,
                    tags = viewModel.tags.value
            )
            favouritesViewModel.add(station)
        }
    }
}