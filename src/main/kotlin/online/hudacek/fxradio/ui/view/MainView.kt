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

package online.hudacek.fxradio.ui.view

import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.stage.Window
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.util.set
import online.hudacek.fxradio.ui.view.library.LibraryView
import online.hudacek.fxradio.ui.view.player.PlayerView
import online.hudacek.fxradio.ui.view.stations.StationsView
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Property
import tornadofx.addClass
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane
import tornadofx.hgrow
import tornadofx.splitpane
import tornadofx.vbox

/**
 * Entry to the app, parent to all other views inside it
 */
class MainView : BaseView(FxRadio.APP_NAME) {

    private val windowDividerProperty by lazy { Property(Properties.WindowDivider) }

    // Main views of the app
    private val playerView: PlayerView by inject()
    private val stationsView: StationsView by inject()
    private val menuBarView: MenuBarView by inject()
    private val libraryView: LibraryView by inject()

    // Right pane of the app (Player + Stations)
    private val rightPane by lazy {
        vbox {
            hgrow = Priority.NEVER
            add(playerView)
            add(stationsView)
        }
    }

    private val splitPane by lazy {
        splitpane(Orientation.HORIZONTAL, libraryView.root, rightPane) {

            // Constrains width of left pane
            libraryView.root.minWidthProperty().bind(widthProperty().divide(6))
            libraryView.root.maxWidthProperty().bind(widthProperty().multiply(0.22))

            // Remove 1px border from SplitPane
            addClass(Styles.noBorder)
        }
    }

    override val root = vbox {
        setPrefSize(800.0, 600.0)
        add(menuBarView)

        notificationPane(showFromTop = true) {
            isCloseButtonVisible = false
            appEvent.appNotification.subscribe { this[it.glyph] = it.title }

            content {
                add(splitPane)
                with(splitPane) {
                    prefWidthProperty().bind(this@vbox.widthProperty())
                    prefHeightProperty().bind(this@vbox.heightProperty())
                }
            }
        }
    }

    override fun onDock() {
        with(splitPane) {
            // Workaround for setting correct position of divider after restart of app
            setDividerPositions(windowDividerProperty.get(0.30))
        }

        primaryStage.setOnCloseRequest { e ->
            // Prevent window from closing when more windows are opened
            Window.getWindows().let {
                if (it.size > 1) {
                    it.last().requestFocus()
                    e.consume()
                }
            }
        }
    }

    override fun onUndock() {
        // Save divider position before closing the app
        windowDividerProperty.save(splitPane.dividers[0].position)
    }
}
