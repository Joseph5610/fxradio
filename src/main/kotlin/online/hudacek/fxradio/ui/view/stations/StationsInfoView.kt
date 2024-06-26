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
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.media.StreamMetaData
import online.hudacek.fxradio.persistence.cache.StationImageCache
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.menu.platformContextMenu
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.make
import online.hudacek.fxradio.ui.util.msgFormat
import online.hudacek.fxradio.ui.util.openUrl
import online.hudacek.fxradio.ui.util.showWhen
import online.hudacek.fxradio.ui.util.smallLabel
import online.hudacek.fxradio.ui.util.stationView
import online.hudacek.fxradio.usecase.GetCoverArtUseCase
import online.hudacek.fxradio.util.actionEvents
import online.hudacek.fxradio.util.macos.MacUtils
import online.hudacek.fxradio.util.macos.NsMenu
import online.hudacek.fxradio.util.observeOnFx
import online.hudacek.fxradio.util.toBinding
import online.hudacek.fxradio.viewmodel.FavouritesViewModel
import online.hudacek.fxradio.viewmodel.SelectedStation
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.action
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.button
import tornadofx.center
import tornadofx.controlsfx.glyph
import tornadofx.flowpane
import tornadofx.get
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.insets
import tornadofx.item
import tornadofx.label
import tornadofx.listview
import tornadofx.paddingAll
import tornadofx.paddingTop
import tornadofx.putString
import tornadofx.region
import tornadofx.separator
import tornadofx.sizeProperty
import tornadofx.stringBinding
import tornadofx.style
import tornadofx.tooltip
import tornadofx.top
import tornadofx.vbox
import tornadofx.vgrow
import java.time.format.DateTimeFormatter
import java.util.*

private const val LOGO_SIZE = 60.0
private const val EMPTY_LIST_ICON_SIZE = 45.0
private const val ICON_SIZE = 12.0
private const val COVER_ART_SIZE = 25.0
private const val GLYPH_SIZE = 14.0
private const val YT_URL = "https://www.youtube.com/results?search_query="

data class MetaDataWithStation(val station: Station, val metaData: StreamMetaData)

class StationsInfoView : BaseView() {

    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val favouritesViewModel: FavouritesViewModel by inject()
    private val coverArtUseCase: GetCoverArtUseCase by inject()

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
    private val playlistLargeIcon by lazy { FontAwesome.Glyph.HISTORY.make(EMPTY_LIST_ICON_SIZE, isPrimary = false) }

    private val stationLogo by lazy {
        stationView(selectedStationViewModel.stationObservable, LOGO_SIZE) {
            subscribe()
        }
    }

    private val stationNameBinding = selectedStationViewModel.countryCodeProperty.stringBinding {
        Locale.of("", it).displayName
    }

    private val historyListView = listview<MetaDataWithStation> {
        vgrow = Priority.ALWAYS

        cellCache {
            hbox(spacing = 5, alignment = Pos.CENTER_LEFT) {
                tooltip(it.metaData.nowPlaying)
                val menu: Menu = platformContextMenu {
                    item(messages["playlistHistory.play"].msgFormat(it.station.name)) {
                        action {
                            selectedStationViewModel.item = SelectedStation(it.station)
                        }
                    }
                    separator()
                    item(messages["playlistHistory.searchOnYouTube"]) {
                        action {
                            app.openUrl(YT_URL, it.metaData.nowPlaying)
                        }
                    }
                    separator()
                    item(messages["copy"]) {
                        action {
                            clipboard.putString(it.metaData.nowPlaying)
                        }
                    }
                }
                imageview(StationImageCache.defaultStationLogo) {
                    isPreserveRatio = true
                    fitWidth = COVER_ART_SIZE
                    fitHeight = COVER_ART_SIZE

                    coverArtUseCase.execute(it.metaData.nowPlaying)
                        .subscribe {
                            if (it.isSuccessful) {
                                it.body?.byteStream().use { i ->
                                    image = Image(i, 80.0, 80.0, true, true)
                                }
                            }
                        }
                }
                vbox {
                    label(it.metaData.nowPlaying) {
                        maxWidth = 180.0
                    }
                    smallLabel(it.metaData.timestamp.format(formatter) + " | " + it.station.name) {
                        maxWidth = 180.0
                    }
                }
                region {
                    hgrow = Priority.ALWAYS
                }

                glyph {
                    graphic = FontAwesome.Glyph.ELLIPSIS_H.make(GLYPH_SIZE)
                    // Show standard javafx context menu on non-macos OS
                    val cMenu by lazy { ContextMenu().apply { items.addAll(menu.items) } }
                    setOnMouseClicked {
                        if (!MacUtils.isMac) {
                            cMenu.show(this, Side.BOTTOM, layoutX, layoutY)
                        } else {
                            NsMenu.showContextMenu(menu, it)
                        }
                    }
                }
            }
        }

        cellFormat {
            addClass(Styles.decoratedListItem)
        }

        appEvent.streamMetaDataUpdates
            .filter { it.nowPlaying.length > 1 }
            .observeOnFx()
            // Replace Station name received from stream with API station name
            .withLatestFrom(selectedStationViewModel.stationObservable) { m, s ->
                MetaDataWithStation(s, m)
            }
            .subscribe {
                items.add(0, it)
            }

        showWhen {
            selectedStationViewModel.showPlaylistProperty.and(items.sizeProperty.greaterThan(0))
        }

        addClass(Styles.decoratedListView)
    }

    override val root = borderpane {
        opacity = 0.98
        prefWidth = 290.0
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
                    tooltip(messages["info.visitWebsite"])
                }

                smallLabel(messages["verified"]) {
                    graphic = FontAwesome.Glyph.CHECK_CIRCLE.make(size = ICON_SIZE)
                    addClass(Styles.tag)
                    showWhen { selectedStationViewModel.hasExtendedInfoProperty }
                }

                label(stationNameBinding) {
                    paddingTop = 5.0
                    addClass(Styles.grayLabel)
                }

                showWhen {
                    selectedStationViewModel.showPlaylistProperty.not()
                }
            }
        }

        center {
            vbox {
                add(historyListView)

                vbox(alignment = Pos.CENTER) {
                    vgrow = Priority.ALWAYS
                    add(playlistLargeIcon)
                    label(messages["playlistHistory.empty"]) {
                        addClass(Styles.subheader)
                    }
                    showWhen {
                        selectedStationViewModel.showPlaylistProperty.and(
                            historyListView.items.sizeProperty.isEqualTo(0)
                        )
                    }
                }

                vbox {
                    maxWidth = 250.0
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
                        add(
                            find<TagsFragment>(
                                params = mapOf(
                                    "tagsProperty" to selectedStationViewModel.tagsProperty
                                )
                            )
                        )
                        showWhen {
                            selectedStationViewModel.tagsProperty.sizeProperty.isNotEqualTo(0)
                        }
                    }

                    showWhen {
                        selectedStationViewModel.showPlaylistProperty.not()
                    }
                }
            }
        }

        bottom {
            vbox(spacing = 5.0, alignment = Pos.CENTER) {
                button(messages["copy.streamUrl"], graphic = copyIcon) {
                    maxWidth = Double.MAX_VALUE

                    actionEvents()
                        .map { selectedStationViewModel.stationProperty.value }
                        .subscribe {
                            clipboard.putString(it.urlResolved)
                        }
                }

                separator()

                button(messages["menu.station.favourite"], graphic = favouriteAddIcon) {
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

                button(messages["menu.station.favouriteRemove"], graphic = favouriteRemoveIcon) {
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

                button(messages["menu.station.vote"], graphic = likeIcon) {
                    maxWidth = Double.MAX_VALUE

                    actionEvents()
                        .map { selectedStationViewModel.stationProperty.value }
                        .subscribe(appEvent.votedStations)

                    addClass(Styles.primaryButton)
                }

                showWhen {
                    selectedStationViewModel.showPlaylistProperty.not()
                }
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }

    override fun onDock() {
        selectedStationViewModel.retrieveAdditionalData()
    }

    private fun createInfoLabel(key: String, valueProperty: Property<*>?) = valueProperty?.let { p ->
        label(p.stringBinding {
            val value = if (it is String) {
                it.ifEmpty { messages["unknown"] }
            } else it
            messages[key] + ": " + value.toString()
                .split(",")
                .take(2)
                .joinToString()
        }) {
            maxWidth = 190.0
            addClass(Styles.grayLabel)
            addClass(Styles.tag)
        }
    }

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    }
}
