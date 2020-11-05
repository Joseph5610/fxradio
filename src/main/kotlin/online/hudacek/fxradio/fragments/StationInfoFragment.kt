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

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.copyMenu
import online.hudacek.fxradio.utils.createImage
import online.hudacek.fxradio.utils.openUrl
import online.hudacek.fxradio.utils.showWhen
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.StationInfoModel
import online.hudacek.fxradio.viewmodel.StationInfoViewModel
import tornadofx.*
import tornadofx.controlsfx.right
import tornadofx.controlsfx.statusbar

class StationInfoFragment(station: Station? = null) : Fragment() {

    private val stationInfoViewModel: StationInfoViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    init {
        stationInfoViewModel.item = StationInfoModel(station ?: playerViewModel.stationProperty.value)
    }

    override fun onBeforeShow() {
        currentWindow?.opacity = 0.85
    }

    override val root = vbox {
        prefWidth = 300.0
        titleProperty.bind(stationInfoViewModel.stationNameProperty)

        vbox {
            vbox(alignment = Pos.CENTER) {
                paddingAll = 10.0
                imageview {
                    createImage(stationInfoViewModel.stationProperty.value)
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

            stationInfoViewModel.infoItemsProperty.forEach {
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

            stationInfoViewModel.tagsProperty.forEach {
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
            right {
                hyperlink(stationInfoViewModel.homePageProperty) {
                    addClass(Styles.primaryTextColor)
                    action {
                        app.openUrl(text)
                    }
                    copyMenu(clipboard,
                            name = messages["copy"],
                            value = text)
                }
            }

            showWhen {
                stationInfoViewModel.homePageProperty.isNotEmpty
            }
        }
    }
}