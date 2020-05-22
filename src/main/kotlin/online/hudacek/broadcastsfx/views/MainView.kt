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

package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.MainController
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.extension.defaultRadioLogo
import online.hudacek.broadcastsfx.extension.set
import online.hudacek.broadcastsfx.model.PlayerModel
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane


class MainView : View(About.appName) {

    private val controller: MainController by inject()

    private val playerModel: PlayerModel by inject()

    private val playerView: PlayerView by inject()
    private val leftPaneView: LibraryView by inject()
    private val stationsView: StationsView by inject()

    var notification: NotificationPane by singleAssign()

    init {
        setStageIcon(defaultRadioLogo)
    }

    private val rightPane = vbox {
        add(playerView)
        add(stationsView)
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            controller.cancelMediaPlaying()
        }

        if (playerModel.playerType.value == PlayerType.Native) {
            notification[FontAwesome.Glyph.WARNING] = messages["nativePlayerInfo"]
        }
    }

    override val root = vbox {
        setPrefSize(800.0, 600.0)
        vgrow = Priority.ALWAYS
        add(MenuBarView::class)
        notificationPane {
            notification = this
            isShowFromTop = true

            content {
                splitpane(Orientation.HORIZONTAL, leftPaneView.root, rightPane) {
                    prefWidthProperty().bind(this@vbox.widthProperty())
                    prefHeightProperty().bind(this@vbox.heightProperty())
                    setDividerPositions(0.33)
                }
            }
        }
    }
}