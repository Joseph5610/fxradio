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

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import online.hudacek.fxradio.ui.viewmodel.StationInfoModel
import online.hudacek.fxradio.ui.viewmodel.StationInfoViewModel
import online.hudacek.fxradio.utils.copyMenu
import online.hudacek.fxradio.utils.openUrl
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.utils.stationImage
import tornadofx.*
import tornadofx.controlsfx.left
import tornadofx.controlsfx.right
import tornadofx.controlsfx.statusbar

class StationInfoFragment(station: Station? = null) : Fragment() {

    private val viewModel: StationInfoViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    init {
        viewModel.item = StationInfoModel(station ?: playerViewModel.stationProperty.value)
    }

    override val root = vbox {
        prefWidth = 300.0
        titleProperty.bind(viewModel.stationNameProperty)

        vbox {
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
        }

        flowpane {
            hgap = 5.0
            vgap = 5.0
            alignment = Pos.CENTER
            paddingAll = 5.0

            viewModel.infoItemsProperty.forEach {
                //Don't display not not relevant values
                if (it.value != "0") {
                    val text =
                            if (it.key.isNotEmpty()) messages[it.key] + ": " + it.value
                            else it.value

                    label(text) {
                        addClass(Styles.grayLabel)
                        addClass(Styles.tag)
                        copyMenu(clipboard,
                                name = messages["copy"],
                                value = it.value)
                    }
                }
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

        statusbar {
            left {
                hyperlink(messages["menu.station.vote"]) {
                    action {
                        viewModel.addVote.onNext(viewModel.stationProperty.value)
                    }
                    addClass(Styles.primaryTextColor)
                }
            }
            right {
                hyperlink(viewModel.homePageProperty) {
                    action {
                        app.openUrl(text)
                    }
                    copyMenu(clipboard,
                            name = messages["copy"],
                            value = text)
                    addClass(Styles.primaryTextColor)
                }
            }

            showWhen {
                viewModel.homePageProperty.isNotEmpty
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}