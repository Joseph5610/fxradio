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

package online.hudacek.fxradio.fragments

import javafx.scene.layout.Priority
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.AddStationBody
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.set
import online.hudacek.fxradio.viewmodel.AddStationModel
import online.hudacek.fxradio.viewmodel.AddStationViewModel
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.bindAutoCompletion
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane

class AddStationFragment : Fragment() {

    private val viewModel: AddStationViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    //Bind Countries object to just country name
    private val autoCompleteCountries = observableListOf<String>()

    init {
        autoCompleteCountries.bind(libraryViewModel.countriesProperty) { it.name }
    }

    override val root = notificationPane {
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
                                if (viewModel.validate(viewModel.name, maxValue = 400))
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
                                if (viewModel.validate(viewModel.homepage, minValue = 7))
                                    null
                                else
                                    error(messages["field.invalid.length"])

                            }
                            promptText = "https://example.com/"
                        }
                    }
                    field(messages["add.url"]) {
                        textfield(viewModel.url) {
                            required()
                            validator {
                                if (viewModel.validate(viewModel.url, minValue = 7))
                                    null
                                else
                                    error(messages["field.invalid.length"])
                            }
                            promptText = "https://example.com/stream.m3u"
                        }
                    }
                    field(messages["add.icon"]) {
                        textfield(viewModel.favicon) {
                            required()
                            validator {
                                if (viewModel.validate(viewModel.favicon, minValue = 7))
                                    null
                                else
                                    error(messages["field.invalid.length"])
                            }
                            promptText = "https://example.com/favicon.ico"
                        }
                    }
                    field(messages["add.language"]) {
                        textfield(viewModel.language) {
                            required()
                            validator {
                                if (viewModel.validate(viewModel.language))
                                    null
                                else
                                    error(messages["field.invalid.length"])
                            }
                            promptText = messages["add.language.prompt"]
                        }
                    }
                    field(messages["add.country"]) {
                        textfield(viewModel.country) {
                            bindAutoCompletion(autoCompleteCountries)
                            required()
                            promptText = messages["add.country.prompt"]
                        }
                    }
                    field(messages["add.tags"]) {
                        textfield(viewModel.tags) {
                            promptText = messages["add.tags.prompt"]
                        }
                    }
                }

                hbox(5) {
                    button(messages["save"]) {
                        enableWhen(viewModel.valid)
                        isDefaultButton = true
                        addClass(Styles.primaryButton)
                        action {
                            viewModel.commit {
                                viewModel.item = AddStationModel(AddStationBody())
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
                                        .subscribe({
                                            fire(NotificationEvent(messages["add.success"]))
                                            close()
                                        }, {
                                            this@notificationPane[FontAwesome.Glyph.CHECK] = messages["add.error"]
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
    }
}