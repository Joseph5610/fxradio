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

package online.hudacek.fxradio.ui.view.stations

import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.beans.property.Property
import javafx.geometry.Pos
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.openUrl
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.stationImage
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SearchViewModel
import online.hudacek.fxradio.viewmodel.StationInfoViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.bindChildren
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.button
import tornadofx.center
import tornadofx.flowpane
import tornadofx.get
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.stringBinding
import tornadofx.top
import tornadofx.vbox

class StationsInfoView : BaseView(FxRadio.appName) {

    private val stationInfoViewModel: StationInfoViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val stationLogo by lazy {
        imageview {
            fitHeight = 60.0
            fitHeight = 60.0
        }
    }

    override fun onDock() {
        stationInfoViewModel.stationProperty.stationImage(stationLogo)
    }

    override val root = borderpane {
        prefWidth = 250.0
        paddingAll = 10.0

        top {
            vbox {
                vbox(alignment = Pos.CENTER) {
                    paddingAll = 10.0
                    add(stationLogo)

                    hyperlink(stationInfoViewModel.nameProperty) {
                        action {
                            app.openUrl(stationInfoViewModel.homePageProperty.value)
                        }
                        addClass(Styles.subheader)
                        addClass(Styles.primaryTextColor)
                    }
                }
            }
        }

        center {
            vbox {
                smallLabel("Station Details")
                flowpane {
                    hgap = 5.0
                    vgap = 5.0
                    alignment = Pos.CENTER
                    paddingAll = 5.0

                    label(createInfoBinding("info.bitrate", stationInfoViewModel.bitrateProperty)) {
                        addClass(Styles.grayLabel)
                        addClass(Styles.tag)
                    }
                    label(createInfoBinding("info.codec", stationInfoViewModel.codecProperty)) {
                        addClass(Styles.grayLabel)
                        addClass(Styles.tag)
                    }
                    label(createInfoBinding("info.votes", stationInfoViewModel.votesProperty)) {
                        addClass(Styles.grayLabel)
                        addClass(Styles.tag)
                    }
                    label(createInfoBinding("info.language", stationInfoViewModel.languageProperty)) {
                        addClass(Styles.grayLabel)
                        addClass(Styles.tag)
                    }
                    label(createInfoBinding("info.country", stationInfoViewModel.countryProperty)) {
                        addClass(Styles.grayLabel)
                        addClass(Styles.tag)
                    }
                }

                smallLabel("Tags")
                flowpane {
                    hgap = 5.0
                    vgap = 5.0
                    paddingAll = 5.0
                    alignment = Pos.CENTER
                    bindChildren(stationInfoViewModel.tagsProperty) {
                        hyperlink(it) {
                            addClass(Styles.tag)
                            addClass(Styles.grayLabel)

                            action {
                                libraryViewModel.stateProperty.value = LibraryState.Search
                                searchViewModel.bindQueryProperty.value = it
                                searchViewModel.searchByTagProperty.value = true
                            }
                        }
                    }
                }
            }
        }

        bottom {
            vbox(alignment = Pos.CENTER) {
                button(messages["menu.station.vote"]) {
                    maxWidth = Double.MAX_VALUE
                    actionEvents().map { stationInfoViewModel.stationProperty.value }.subscribe(appEvent.addVote)

                    addClass(Styles.primaryButton)
                }
            }
        }

        addClass(Styles.backgroundWhiteSmoke)
    }

    private fun createInfoBinding(key: String, valueProperty: Property<*>) = valueProperty.stringBinding {
        val value = if (it is String) {
            it.ifEmpty { messages["unknown"] }
        } else if (it is Int) {
            if (it == 0) messages["unknown"]
            else it
        } else it
        messages[key] + ": " + value
    }
}