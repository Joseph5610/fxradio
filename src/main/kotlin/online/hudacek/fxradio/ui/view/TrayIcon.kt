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

import javafx.application.Platform
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.util.toObservable
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.PreferencesViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.Controller
import tornadofx.get
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

    private val showIconObservable = preferencesViewModel.useTrayIconProperty.toObservable()

    private fun createIcon() = with(app) {
        trayicon(resources.stream(Config.Resources.trayIcon), tooltip = FxRadio.appName, autoSize = true) {
            trayIcon = this

            setOnMouseClicked(fxThread = true) {
                showPrimaryStage()
            }

            menu(FxRadio.appName) {
                item(messages["show"] + " " + FxRadio.appName) {
                    setOnAction(fxThread = true) {
                        showPrimaryStage()
                    }
                }
                addSeparator()

                item(selectedStationViewModel.nameProperty.value) {
                    selectedStationViewModel.stationObservable.subscribe {
                        label = it.name
                    }
                    isEnabled = false
                }

                item("Play/Stop") {
                    playerViewModel.stateObservable.subscribe {
                        label = if (it is PlayerState.Playing) {
                            messages["menu.player.stop"]
                        } else {
                            messages["menu.player.start"]
                        }
                    }
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

    private fun showPrimaryStage() {
        primaryStage.show()
        primaryStage.toFront()
    }

    fun subscribe() = showIconObservable.subscribe {
        if (it) {
            createIcon()
        } else {
            removeIcon()
        }
    }
}
