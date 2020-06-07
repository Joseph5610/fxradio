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
import online.hudacek.broadcastsfx.extension.ui.copyMenu
import online.hudacek.broadcastsfx.extension.ui.createImage
import online.hudacek.broadcastsfx.extension.ui.openUrl
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import tornadofx.*
import tornadofx.controlsfx.statusbar

class StationInfoFragment(station: Station? = null) : Fragment() {

    private val playerModel: PlayerModel by inject()

    private val shownStation: Station = station ?: playerModel.station.value

    private val items = observableListOf<String>()

    override fun onBeforeShow() {
        currentWindow?.opacity = 0.85
    }

    override val root = vbox {
        prefWidth = 300.0
        title = shownStation.name
        shownStation.let {
            val tagsList = it.tags.split(",")
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }

            vbox {
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

            flowpane {
                hgap = 5.0
                vgap = 5.0
                alignment = Pos.CENTER
                paddingAll = 5.0

                if (it.votes != 0) items.add("${it.votes} votes")

                val codec = if (it.bitrate != 0) {
                    it.codec + " (${it.bitrate} kbps)"
                } else {
                    it.codec
                }

                items.addAll(
                        codec,
                        "Country: ${it.country}",
                        "Language: ${it.language}"
                )
                items.forEach { info ->
                    label(info) {
                        addClass(Styles.grayLabel)
                        addClass(Styles.tag)
                        copyMenu(clipboard, value = info)
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
                            copyMenu(clipboard, value = tag)
                        }
                    }
                }
            }

            if (it.homepage.isNotEmpty()) {
                statusbar {
                    alignment = Pos.BOTTOM_RIGHT
                    rightItems.add(
                            hyperlink(it.homepage) {
                                addClass(Styles.primaryTextColor)
                                action {
                                    app.openUrl(it.homepage)
                                }
                                copyMenu(clipboard, value = it.homepage)
                            }
                    )
                }
            }
        }
    }
}