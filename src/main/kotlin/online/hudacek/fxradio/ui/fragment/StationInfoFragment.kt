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
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.StationInfo
import online.hudacek.fxradio.viewmodel.StationInfoViewModel
import tornadofx.*

class StationInfoFragment : BaseFragment() {

    private val viewModel: StationInfoViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    init {
        viewModel.item = StationInfo(playerViewModel.stationProperty.value)
    }

    override val root = vbox {
        prefWidth = 400.0
        titleProperty.bind(viewModel.nameProperty)

        hbox(10) {
            vbox(alignment = Pos.CENTER) {
                paddingAll = 10.0
                imageview {
                    viewModel.stationProperty.stationImage(this)
                    effect = DropShadow(30.0, Color.LIGHTGRAY)
                    fitHeight = 100.0
                    fitHeight = 100.0
                    isPreserveRatio = true
                }
            }
            vbox {
                alignment = Pos.CENTER
                label(viewModel.nameProperty) {
                    addClass(Styles.subheader)
                }

                hyperlink(viewModel.homePageProperty) {
                    action {
                        app.openUrl(text)
                    }
                    copyMenu(clipboard,
                            name = messages["copy"],
                            value = text)

                    showWhen {
                        viewModel.homePageProperty.isNotEmpty
                    }
                }

                flowpane {
                    hgap = 5.0
                    vgap = 5.0
                    alignment = Pos.CENTER
                    paddingAll = 5.0

                    viewModel.tagsProperty.forEach {
                        label(it) {
                            addClass(Styles.tag)
                            addClass(Styles.grayLabel)
                            copyMenu(clipboard,
                                    name = messages["copy"],
                                    value = it)
                        }
                    }
                }
            }
        }

        vbox {
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
        }

        vbox {
            prefHeight = 210.0
            paddingBottom = 10.0
            webview {
                engine.load(Config.API.mapURL +
                        "?lat=${viewModel.stationProperty.value.geo_lat}" +
                        "&lon=${viewModel.stationProperty.value.geo_long}"
                )
            }

            showWhen {
                viewModel.stationProperty.booleanBinding {
                    it!!.geo_lat != 0.0 && it.geo_long != 0.0
                }.and(booleanProperty(Config.Flags.enableMap))
            }
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
            backgroundColor += Color.WHITESMOKE
        }
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