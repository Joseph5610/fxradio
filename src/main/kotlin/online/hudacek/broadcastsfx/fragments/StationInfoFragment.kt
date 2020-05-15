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

package online.hudacek.broadcastsfx.fragments

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.extension.createImage
import online.hudacek.broadcastsfx.extension.openUrl
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*
import tornadofx.controlsfx.statusbar

class StationInfoFragment(val station: Station? = null, showImage: Boolean = true, showList: Boolean = true) : Fragment() {

    private val playerModel: PlayerModel by inject()

    private val showStation: Station = station ?: playerModel.station.value

    override fun onBeforeShow() {
        currentStage?.opacity = 0.85
    }

    override val root = vbox {
        if (showList) {
            setPrefSize(300.0, 300.0)
        } else {
            prefWidth = 300.0
        }

        showStation.let {
            title = it.name

            val tagsList = it.tags.split(",")
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }

            val codecBitrateInfo = it.codec + " (" + it.bitrate + " kbps)"

            if (showImage) {
                vbox(alignment = Pos.CENTER) {
                    paddingAll = 10.0
                    imageview {
                        createImage(it)
                        effect = DropShadow(30.0, Color.LIGHTGRAY)
                        fitHeight = 100.0
                        fitHeight = 100.0
                        isPreserveRatio = true
                    }
                }
            }

            if (tagsList.isNotEmpty()) {
                flowpane {
                    hgap = 5.0
                    vgap = 5.0
                    alignment = Pos.CENTER
                    paddingAll = 5.0
                    tagsList.forEach { tag ->
                        label(tag) {
                            addClass(Styles.tag)
                            addClass(Styles.grayLabel)
                        }
                    }
                }
            }

            if (showList) {
                val list = observableListOf(
                        codecBitrateInfo,
                        "Country: ${it.country}",
                        "Language: ${it.language}")
                listview(list)
            }

            if (it.homepage.isNotEmpty()) {
                statusbar {
                    rightItems.add(
                            hyperlink(it.homepage) {
                                addClass(Styles.primaryTextColor)
                                action {
                                    app.openUrl(it.homepage)
                                }
                            }
                    )
                }
            }
        }
    }
}