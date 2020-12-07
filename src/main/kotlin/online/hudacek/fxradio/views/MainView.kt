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

package online.hudacek.fxradio.views

import javafx.geometry.Orientation
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import online.hudacek.fxradio.*
import online.hudacek.fxradio.api.HttpClientHolder
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.VCSApi
import online.hudacek.fxradio.styles.Styles
import online.hudacek.fxradio.utils.show
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.views.menu.MenuBarView
import online.hudacek.fxradio.views.player.PlayerMainView
import online.hudacek.fxradio.views.stations.StationsMainView
import org.controlsfx.control.NotificationPane
import tornadofx.*
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane

/**
 * Main View
 * --------------
 * Entry to the app, parent to all other views inside main window
 */
class MainView : View(FxRadio.appName) {

    private val playerViewModel: PlayerViewModel by inject()

    //Main views of the app
    private val menuBarView: MenuBarView by inject()
    private val libraryView: LibraryView by inject()
    private val playerMainView: PlayerMainView by inject()
    private val stationsMainView: StationsMainView by inject()

    init {
        setStageIcon(Image(Config.Resources.stageIcon))
    }

    override fun onDock() {
        //Correctly shutdown all classes
        currentStage?.setOnCloseRequest {
            playerViewModel.releasePlayer()
            StationsApi.client.shutdown()
            VCSApi.client.shutdown()
            HttpClientHolder.client.shutdown()
        }
    }

    //Right pane of the app (Player + Stations)
    private val rightPane by lazy {
        vbox {
            hgrow = Priority.NEVER
            add(playerMainView)
            add(stationsMainView)
        }
    }

    override val root = vbox {
        setPrefSize(800.0, 600.0)
        add(menuBarView)

        notificationPane {
            isShowFromTop = true

            //Show dark notifications
            if (FxRadio.isDarkModeAppStyle) {
                styleClass.add(NotificationPane.STYLE_CLASS_DARK)
            }

            subscribe<NotificationEvent> {
                show(it.glyph, it.text, it.op)
            }

            content {
                splitpane(Orientation.HORIZONTAL, libraryView.root, rightPane) {
                    setDividerPositions(Property(Properties.WINDOW_DIVIDER).get(0.30))
                    prefWidthProperty().bind(this@vbox.widthProperty())
                    prefHeightProperty().bind(this@vbox.heightProperty())

                    //Save position of divider to config file
                    dividers[0].positionProperty().onChange {
                        Property(Properties.WINDOW_DIVIDER).save(it)
                    }

                    //Constrains width of left pane
                    libraryView.root.minWidthProperty().bind(widthProperty().divide(5))
                    libraryView.root.maxWidthProperty().bind(widthProperty().multiply(0.35))

                    //Remove 1px border from splitpane
                    addClass(Styles.noBorder)
                }
            }
        }
    }
}