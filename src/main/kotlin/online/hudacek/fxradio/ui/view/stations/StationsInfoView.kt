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

import io.reactivex.rxjava3.core.Observable
import javafx.beans.property.Property
import javafx.geometry.Pos
import javafx.scene.paint.Color
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.openUrl
import online.hudacek.fxradio.ui.util.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.ui.util.smallLabel
import online.hudacek.fxradio.ui.util.stationView
import online.hudacek.fxradio.util.actionEvents
import online.hudacek.fxradio.util.toBinding
import online.hudacek.fxradio.viewmodel.FavouritesViewModel
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SearchViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.FX
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
import tornadofx.insets
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingTop
import tornadofx.putString
import tornadofx.separator
import tornadofx.sizeProperty
import tornadofx.stringBinding
import tornadofx.style
import tornadofx.tooltip
import tornadofx.top
import tornadofx.vbox

private const val LOGO_SIZE = 60.0
private const val ICON_SIZE = 12.0

class StationsInfoView : BaseView(FxRadio.appName) {

    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()

    private val likeIcon by lazy {
        FontAwesome.Glyph.THUMBS_UP.make(ICON_SIZE) {
            style {
                textFill = Color.WHITESMOKE
            }
        }
    }

    private val copyIcon by lazy { FontAwesome.Glyph.COPY.make(ICON_SIZE, isPrimary = false) }
    private val favouriteAddIcon by lazy { FontAwesome.Glyph.HEART.make(ICON_SIZE, isPrimary = false) }
    private val favouriteRemoveIcon by lazy { FontAwesome.Glyph.HEART_ALT.make(ICON_SIZE, isPrimary = false) }

    private val stationLogo by lazy {
        stationView(selectedStationViewModel.stationObservable, LOGO_SIZE) {
            subscribe()
        }
    }

    override fun onDock() {
        selectedStationViewModel.retrieveAdditionalData()
    }

    override val root = borderpane {
        opacity = 0.985
        prefWidth = 250.0
        padding = insets(top = 30.0, left = 10.0, right = 10.0, bottom = 15.0)
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

                smallLabel(messages["verified"]) {
                    graphic = FontAwesome.Glyph.CHECK_CIRCLE.make(size = 13.0, isPrimary = true)
                    addClass(Styles.tag)
                    showWhen { selectedStationViewModel.hasExtendedInfoProperty }
                }

                label(selectedStationViewModel.countryProperty) {
                    paddingTop = 5.0
                    addClass(Styles.grayLabel)
                }
            }
        }

        center {
            vbox {
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
                    createInfoLabel("info.clickTrend", selectedStationViewModel.clickTrendProperty)?.let { add(it) }
                    createInfoLabel("info.clickCount", selectedStationViewModel.clickCountProperty)?.let { add(it) }
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
                            clipboard.putString(it.urlResolved)
                        }
                }

                separator()

                button(messages["menu.station.favourite"]) {
                    graphic = favouriteAddIcon
                    maxWidth = Double.MAX_VALUE

                    actionEvents()
                        .map { selectedStationViewModel.stationProperty.value }
                        .filter { it !in favouritesViewModel.stationsProperty }
                        .subscribe(favouritesViewModel::addFavourite)

                    showWhen {
                        Observable.combineLatest(
                            favouritesViewModel.stationsObservable,
                            selectedStationViewModel.stationObservable
                        ) { list, station ->
                            !list.contains(station)
                        }.toBinding()
                    }
                }

                button(messages["menu.station.favouriteRemove"]) {
                    graphic = favouriteRemoveIcon
                    maxWidth = Double.MAX_VALUE

                    actionEvents()
                        .map { selectedStationViewModel.stationProperty.value }
                        .filter { it in favouritesViewModel.stationsProperty }
                        .subscribe(favouritesViewModel::removeFavourite)


                    showWhen {
                        Observable.combineLatest(
                            favouritesViewModel.stationsObservable,
                            selectedStationViewModel.stationObservable
                        ) { list, station ->
                            list.contains(station)
                        }.toBinding()
                    }
                }

                button(messages["menu.station.vote"]) {
                    requestFocusOnSceneAvailable()
                    graphic = likeIcon
                    maxWidth = Double.MAX_VALUE

                    actionEvents()
                        .map { selectedStationViewModel.stationProperty.value }
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
                .replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase(FX.locale) else c.toString() }
        }) {
            addClass(Styles.grayLabel)
            addClass(Styles.tag)
        }
    }
}
