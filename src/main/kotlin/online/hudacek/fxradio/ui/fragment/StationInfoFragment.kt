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
import javafx.beans.property.IntegerProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.effect.DropShadow
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.ui.*
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.*
import tornadofx.*

class StationInfoFragment : BaseFragment() {

    private val viewModel: StationInfoViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    init {
        viewModel.item = StationInfo(playerViewModel.stationProperty.value)
    }

    override fun onBeforeShow() {
        viewModel.stationProperty.stationImage(stationLogo)
    }

    private val stationLogo by lazy {
        imageview {
            fitHeight = 60.0
            fitHeight = 60.0
        }
    }

    private val webview by lazy {
        webview {
            prefHeight = 210.0
            engine.load(Config.API.mapURL +
                    "?lat=${viewModel.stationProperty.value.geo_lat}" +
                    "&lon=${viewModel.stationProperty.value.geo_long}"
            )
            showWhen {
                viewModel.stationProperty.booleanBinding {
                    it!!.geo_lat != 0.0 && it.geo_long != 0.0
                }.and(booleanProperty(Config.Flags.enableMap))
            }
        }
    }

    override val root = vbox {
        vgrow = Priority.ALWAYS
        prefWidth = 400.0
        titleProperty.bind(viewModel.nameProperty)

        hbox(10) {
            vbox(alignment = Pos.CENTER) {
                paddingAll = 10.0
                add(stationLogo)
            }
            vbox {
                alignment = Pos.CENTER
                hyperlink(viewModel.nameProperty) {
                    requestFocusOnSceneAvailable()
                    action {
                        app.openUrl(viewModel.homePageProperty.value)
                    }
                    showWhen {
                        viewModel.homePageProperty.isNotEmpty
                    }
                    addClass(Styles.subheader)
                }

                label(viewModel.nameProperty) {
                    addClass(Styles.subheader)
                    showWhen {
                        viewModel.homePageProperty.isEmpty
                    }
                }

                flowpane {
                    hgap = 5.0
                    vgap = 5.0
                    alignment = Pos.CENTER
                    paddingAll = 5.0

                    viewModel.tagsProperty.forEach {
                        hyperlink(it) {
                            addClass(Styles.tag)
                            addClass(Styles.grayLabel)

                            action {
                                libraryViewModel.stateProperty.value = LibraryState.Search
                                searchViewModel.bindQueryProperty.value = it
                                searchViewModel.searchByTagProperty.value = true
                                close()
                            }
                        }
                    }
                }
            }
        }

        vbox {
            vgrow = Priority.ALWAYS
            paddingBottom = 10.0

            flowpane {
                hgap = 5.0
                vgap = 5.0
                alignment = Pos.CENTER
                paddingAll = 5.0

                createInfoLabel("info.codec", viewModel.codecProperty)?.let { add(it) }
                createInfoLabel("info.bitrate", viewModel.bitrateProperty)?.let { add(it) }
                createInfoLabel("info.language", viewModel.languageProperty)?.let { add(it) }
                createInfoLabel("info.country", viewModel.countryProperty)?.let { add(it) }
                createInfoLabel("info.votes", viewModel.votesProperty)?.let { add(it) }
            }
            add(webview)
        }


        hbox {
            button(messages["menu.station.vote"]) {
                actionEvents()
                        .map { viewModel.stationProperty.value }
                        .subscribe(appEvent.addVote)

                addClass(Styles.primaryButton)
            }
            region {
                hgrow = Priority.ALWAYS
            }

            vbox {
                alignment = Pos.CENTER_RIGHT
                button(messages["close"]) {
                    action {
                        close()
                    }
                }
            }
        }

        style {
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            paddingAll = 8
        }
        addClass(Styles.backgroundWhiteSmoke)
    }

    private fun createInfoLabel(key: String, valueProperty: StringProperty): Label? {
        if (valueProperty.value.isNullOrEmpty()) return null

        val actualTextProperty = valueProperty.stringBinding {
            messages[key] + ": " + it
        }

        return label(actualTextProperty) {
            addClass(Styles.grayLabel)
            addClass(Styles.tag)
            copyMenu(clipboard,
                    name = messages["copy"],
                    value = valueProperty.value)
        }
    }

    private fun createInfoLabel(key: String, valueProperty: IntegerProperty): Label? {
        if (valueProperty.value == 0) return null

        val actualTextProperty = valueProperty.stringBinding {
            messages[key] + ": " + it
        }

        return label(actualTextProperty) {
            addClass(Styles.grayLabel)
            addClass(Styles.tag)
        }
    }
}