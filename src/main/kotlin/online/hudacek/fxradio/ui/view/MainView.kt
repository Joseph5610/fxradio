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
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.set
import online.hudacek.fxradio.ui.stylableNotificationPane
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.view.library.LibraryView
import online.hudacek.fxradio.ui.view.player.PlayerView
import online.hudacek.fxradio.ui.view.stations.StationsView
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.Property
import online.hudacek.fxradio.viewmodel.DarkModeViewModel
import org.controlsfx.control.NotificationPane
import tornadofx.*
import tornadofx.controlsfx.content

/**
 * Entry to the app, parent to all other views inside it
 */
class MainView : BaseView(FxRadio.appName) {

    private val windowDividerProperty = Property(Properties.WindowDivider)
    private val darkModeViewModel: DarkModeViewModel by inject()

    // Main views of the app
    private val menuBarView: MenuBarView by inject()
    private val libraryView: LibraryView by inject()
    private val playerView: PlayerView by inject()
    private val stationsView: StationsView by inject()

    init {
        setStageIcon(Image(Config.Resources.stageIcon))
    }

    override fun onDock() {
        with(leftPane) {
            // Workaround for setting correct position of divider after restart of app
            setDividerPositions(windowDividerProperty.get(0.30))
        }
    }

    override fun onUndock() {
        // Save divider position before closing the app
        windowDividerProperty.save(leftPane.dividers[0].position)
    }

    // Right pane of the app (Player + Stations)
    private val rightPane by lazy {
        vbox {
            hgrow = Priority.NEVER
            add(playerView)
            add(stationsView)
        }
    }

    private val leftPane by lazy {
        splitpane(Orientation.HORIZONTAL, libraryView.root, rightPane) {

            // Constrains width of left pane
            libraryView.root.minWidthProperty().bind(widthProperty().divide(5))
            libraryView.root.maxWidthProperty().bind(widthProperty().multiply(0.35))

            // Remove 1px border from SplitPane
            addClass(Styles.noBorder)
        }
    }

    override val root = vbox {
        setPrefSize(800.0, 600.0)
        add(menuBarView)

        stylableNotificationPane {
            appEvent.appNotification
                    .subscribe { this[it.glyph] = it.title }

            darkModeViewModel.darkModeProperty.onChange {
                if (!it) {
                    styleClass -= NotificationPane.STYLE_CLASS_DARK
                } else {
                    styleClass += NotificationPane.STYLE_CLASS_DARK
                }
            }

            content {
                add(leftPane)
                with(leftPane) {
                    prefWidthProperty().bind(this@vbox.widthProperty())
                    prefHeightProperty().bind(this@vbox.heightProperty())
                }
            }
        }
    }
}