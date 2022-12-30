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

import com.github.thomasnield.rxkotlinfx.toObservableChanges
import javafx.application.Platform
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.PreferencesViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.Controller
import tornadofx.get
import tornadofx.stringBinding
import java.awt.MenuItem
import java.awt.SystemTray
import java.awt.TrayIcon
import javax.swing.SwingUtilities

/**
 * Manages system tray icon
 */
class TrayIcon : Controller() {

    private val playerViewModel: PlayerViewModel by inject()
    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val preferencesViewModel: PreferencesViewModel by inject()

    private var trayIcon: TrayIcon? = null
    private var stationItem: MenuItem? = null
    private var playPauseItem: MenuItem? = null

    init {
        selectedStationViewModel.stationObservable.subscribe {
            stationItem?.let { mi ->
                mi.label = it.name
            }
        }

        playerViewModel.stateObservable.subscribe {
            playPauseItem?.let { mi ->
                mi.label = if (it is PlayerState.Playing) {
                    messages["menu.player.stop"]
                } else {
                    messages["menu.player.start"]
                }
            }
        }

        preferencesViewModel.useTrayIconProperty.toObservableChanges()
            .map { it.newVal }
            .subscribe {
                if (it) {
                    addIcon()
                } else {
                    removeIcon()
                }
            }
    }

    fun addIcon() = with(app) {
        if (!preferencesViewModel.useTrayIconProperty.value) return

        trayicon(resources.stream("/" + Config.Resources.stageIcon), tooltip = FxRadio.appName) {
            trayIcon = this

            setOnMouseClicked(fxThread = true) {
                primaryStage.show()
                primaryStage.toFront()
            }

            menu(FxRadio.appName) {
                item(messages["show"] + " " + FxRadio.appName) {
                    setOnAction(fxThread = true) {
                        primaryStage.show()
                        primaryStage.toFront()
                    }
                }
                addSeparator()

                stationItem = item(selectedStationViewModel.nameProperty.value) {
                    isEnabled = false
                }

                playPauseItem = item("Play/Stop") {
                    setOnAction(fxThread = true) {
                        playerViewModel.togglePlayerState()
                    }
                }
                addSeparator()
                item(messages["exit"]) {
                    setOnAction(fxThread = true) {
                        Platform.exit()
                    }
                }
            }
        }
    }

    private fun removeIcon() {
        SwingUtilities.invokeLater {
            trayIcon?.let { SystemTray.getSystemTray().remove(it) }
        }
    }
}
