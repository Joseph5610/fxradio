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

package online.hudacek.fxradio.ui.view.library

import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.geometry.Pos
import mu.KotlinLogging
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.storage.db.Tables
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.Library
import online.hudacek.fxradio.ui.viewmodel.LibraryViewModel
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.property
import tornadofx.*

private val logger = KotlinLogging.logger {}

class LibraryView : View() {
    private val appEvent: AppEvent by inject()

    private val viewModel: LibraryViewModel by inject()

    private val librarySearchView: LibrarySearchView by inject()
    private val libraryCountriesView: LibraryCountriesView by inject()
    private val libraryListView: LibraryListView by inject()
    private val libraryPinnedView: LibraryPinnedView by inject()

    override fun onDock() {
        viewModel.item = Library(
                showLibrary = property(Properties.WINDOW_SHOW_LIBRARY, true),
                showCountries = property(Properties.WINDOW_SHOW_COUNTRIES, true),
                showPinned = property(Properties.WINDOW_SHOW_PINNED, true)
        )
        Tables.pinnedCountries
                .selectAll()
                .subscribe({
                    viewModel.pinnedProperty.add(it)
                }, {
                    logger.error(it) { "Error while getting pinned stations" }
                })
        appEvent.refreshCountries.onNext(Unit)
    }

    override val root = borderpane {
        top {
            vbox {
                vbox {
                    add(librarySearchView)
                    style {
                        padding = box(20.px, 10.px, 20.px, 10.px)
                    }
                }

                add(LibraryTitleFragment(messages["library"], viewModel.showLibraryProperty) {
                    viewModel.showLibraryProperty.value = !viewModel.showLibraryProperty.value
                    viewModel.commit()
                })
                add(libraryListView)

                vbox {
                    add(LibraryTitleFragment(messages["pinned"], viewModel.showPinnedProperty) {
                        viewModel.showPinnedProperty.value = !viewModel.showPinnedProperty.value
                        viewModel.commit()
                    })
                    add(libraryPinnedView)
                    showWhen {
                        viewModel.pinnedProperty.emptyProperty().not()
                    }
                }
            }
        }

        center {
            vbox {
                add(LibraryTitleFragment(messages["countries"], viewModel.showCountriesProperty) {
                    viewModel.showCountriesProperty.value = !viewModel.showCountriesProperty.value
                    viewModel.commit()
                })

                add(libraryCountriesView)

                //Retry link
                vbox(alignment = Pos.CENTER) {
                    hyperlink(messages["downloadRetry"]) {

                        actionEvents()
                                .map { Unit }
                                .subscribe(appEvent.refreshCountries)

                        showWhen {
                            viewModel.countriesProperty.emptyProperty().and(viewModel.showCountriesProperty)
                        }
                    }
                }
                libraryCountriesView.root.prefHeightProperty().bind(heightProperty())
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}