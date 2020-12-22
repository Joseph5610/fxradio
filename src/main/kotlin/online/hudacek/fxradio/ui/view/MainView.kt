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

package online.hudacek.fxradio.ui.view

import javafx.geometry.Orientation
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.view.library.LibraryView
import online.hudacek.fxradio.ui.view.menu.MenuBarView
import online.hudacek.fxradio.ui.view.player.PlayerView
import online.hudacek.fxradio.ui.view.stations.StationsView
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import online.hudacek.fxradio.utils.show
import online.hudacek.fxradio.utils.stylableNotificationPane
import tornadofx.*
import tornadofx.controlsfx.content

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
    private val playerView: PlayerView by inject()
    private val stationsView: StationsView by inject()

    init {
        setStageIcon(Image(Config.Resources.stageIcon))
    }

    override fun onDock() {
        //Correctly shutdown all classes
        currentStage?.setOnCloseRequest {
            playerViewModel.releasePlayer()
            FxRadio.shutdownApp()
        }
    }

    //Right pane of the app (Player + Stations)
    private val rightPane by lazy {
        vbox {
            hgrow = Priority.NEVER
            add(playerView)
            add(stationsView)
        }
    }

    override val root = vbox {
        setPrefSize(800.0, 600.0)
        add(menuBarView)

        stylableNotificationPane {
            subscribe<NotificationEvent> {
                show(it.glyph, it.text, it.op)
            }

            content {
                splitpane(Orientation.HORIZONTAL, libraryView.root, rightPane) {
                    val windowDividerProperty = Property(Properties.WINDOW_DIVIDER)
                    setDividerPositions(windowDividerProperty.get(0.30))
                    prefWidthProperty().bind(this@vbox.widthProperty())
                    prefHeightProperty().bind(this@vbox.heightProperty())

                    //Save position of divider to config file
                    dividers[0].positionProperty().onChange {
                        windowDividerProperty.save(it)
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