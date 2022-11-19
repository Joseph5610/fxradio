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
import javafx.scene.paint.Color
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.make
import online.hudacek.fxradio.ui.openUrl
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.stationView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.update
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SearchViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import org.controlsfx.glyphfont.FontAwesome
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
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.sizeProperty
import tornadofx.stringBinding
import tornadofx.style
import tornadofx.tooltip
import tornadofx.top
import tornadofx.vbox
import java.util.*

private const val LOGO_SIZE = 60.0

class StationsInfoView : BaseView(FxRadio.appName) {

    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val likeIcon by lazy {
        FontAwesome.Glyph.THUMBS_UP.make(12.0) {
            style {
                textFill = Color.WHITESMOKE
            }
        }
    }

    private val copyIcon by lazy {
        FontAwesome.Glyph.COPY.make(12.0, isPrimary = false)
    }

    private val stationLogo by lazy {
        stationView(selectedStationViewModel.stationProperty) {
            fitHeight = LOGO_SIZE
            fitHeight = LOGO_SIZE
        }
    }

    override fun onDock() {
        selectedStationViewModel.retrieveAdditionalData()
    }

    override val root = borderpane {
        prefWidth = 250.0
        paddingAll = 10.0

        top {
            vbox(alignment = Pos.CENTER) {
                paddingAll = 10.0

                add(stationLogo)

                hyperlink(selectedStationViewModel.nameProperty) {
                    action {
                        app.openUrl(selectedStationViewModel.homePageProperty.value)
                    }
                    addClass(Styles.subheader)
                    addClass(Styles.primaryTextColor)
                    tooltip(messages["info.visit.website"])
                }

                label(selectedStationViewModel.countryProperty) {
                    addClass(Styles.grayLabel)
                }
            }

        }

        center {
            vbox {
                smallLabel(messages["info.details"])
                flowpane {
                    hgap = 5.0
                    vgap = 5.0
                    alignment = Pos.CENTER
                    paddingAll = 5.0

                    createInfoLabel("info.bitrate", selectedStationViewModel.bitrateProperty)?.let { add(it) }
                    createInfoLabel("info.codec", selectedStationViewModel.codecProperty)?.let { add(it) }
                    createInfoLabel("info.votes", selectedStationViewModel.votesProperty)?.let { add(it) }
                    createInfoLabel("info.language", selectedStationViewModel.languageProperty)?.let { add(it) }
                    createInfoLabel("info.state", selectedStationViewModel.countryStateProperty)?.let { add(it) }
                    createInfoLabel("info.clicktrend", selectedStationViewModel.clickTrendProperty)?.let { add(it) }
                }

                vbox {
                    smallLabel(messages["info.tags"])
                    flowpane {
                        hgap = 5.0
                        vgap = 5.0
                        paddingAll = 5.0
                        alignment = Pos.CENTER
                        bindChildren(selectedStationViewModel.tagsProperty) {
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
                    showWhen {
                        selectedStationViewModel.tagsProperty.sizeProperty.isNotEqualTo(0)
                    }
                }
            }
        }

        bottom {
            vbox(spacing = 5.0, alignment = Pos.CENTER) {
                button(messages["copy.stream.url"]) {
                    graphic = copyIcon
                    maxWidth = Double.MAX_VALUE

                    actionEvents()
                        .map { selectedStationViewModel.stationProperty.value }
                        .subscribe {
                            clipboard.update(it.urlResolved)
                        }
                }
                button(messages["menu.station.vote"]) {
                    requestFocusOnSceneAvailable()
                    graphic = likeIcon
                    maxWidth = Double.MAX_VALUE

                    actionEvents()
                        .map { selectedStationViewModel.stationProperty.value}
                        .subscribe(appEvent.votedStations)

                    addClass(Styles.primaryButton)
                }
            }
        }

        addClass(Styles.backgroundWhiteSmoke)
    }

    private fun createInfoLabel(key: String, valueProperty: Property<*>?) = valueProperty?.let { p ->
        label(p.stringBinding {
            val value = if (it is String) {
                it.ifEmpty { messages["unknown"] }
            } else it
            messages[key] + ": " + value.toString()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }) {
            addClass(Styles.grayLabel)
            addClass(Styles.tag)
        }
    }
}
