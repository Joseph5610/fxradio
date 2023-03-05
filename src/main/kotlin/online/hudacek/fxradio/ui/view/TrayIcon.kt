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

import com.dustinredmond.fxtrayicon.FXTrayIcon
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.util.toObservable
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.PreferencesViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.Controller
import tornadofx.get

private const val ICON_SIZE = 64

/**
 * Manages system tray icon
 */
class TrayIcon : Controller() {

    private val playerViewModel: PlayerViewModel by inject()
    private val selectedStationViewModel: SelectedStationViewModel by inject()
    private val preferencesViewModel: PreferencesViewModel by inject()

    private val stationMenuItem = MenuItem(Station.dummy.name).apply {
        isDisable = true
    }

    private val playMenuItem = MenuItem("Play/Stop").apply {
        setOnAction {
            playerViewModel.togglePlayerState()
        }
    }

    private val fxTrayIcon by lazy {
        FXTrayIcon.Builder(primaryStage, Image(Config.Resources.trayIcon), ICON_SIZE, ICON_SIZE)
            .applicationTitle(messages["show"] + " " + FxRadio.appName)
            .separator()
            .menuItem(stationMenuItem)
            .menuItem(playMenuItem)
            .separator()
            .addExitMenuItem(messages["exit"])
            .build()
    }

    init {
        selectedStationViewModel.stationObservable.subscribe {
            stationMenuItem.text = it.name
        }

        playerViewModel.stateObservable.subscribe {
            playMenuItem.let { mi ->
                mi.text = if (it is PlayerState.Playing) {
                    messages["menu.player.stop"]
                } else {
                    messages["menu.player.start"]
                }
            }
        }
    }


    /**
     * Add/remove TrayIcon based on user preference
     */
    fun create() {
        if (!FXTrayIcon.isSupported()) return
        preferencesViewModel.useTrayIconProperty.toObservable()
            .subscribe {
                if (it) {
                    fxTrayIcon.show()
                } else {
                    fxTrayIcon.hide()
                }
            }
    }
}
