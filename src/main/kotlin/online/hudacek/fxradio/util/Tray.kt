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

package online.hudacek.fxradio.util

import javafx.application.Platform
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.Component
import tornadofx.get
import tornadofx.onChange
import java.awt.MenuItem
import java.awt.MenuShortcut
import java.awt.event.KeyEvent


/**
 * Adds system tray. Experimental, enabled under flag
 */
class Tray : Component() {

    private val playerViewModel: PlayerViewModel by inject()
    private var stationItem: MenuItem? = null

    init {
        playerViewModel.stationObservable.subscribe {
            stationItem?.let { mi ->
                mi.label = it.name
            }
        }
    }

    fun addIcon() = with(app) {
        if (Config.Flags.useTrayIcon) {
            trayicon(resources.stream("/" + Config.Resources.stageIcon)) {
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

                    stationItem = item(messages["player.streamingStopped"]) {
                        isEnabled = false
                    }

                    item("Play/Stop") {
                        setOnAction(fxThread = true) {
                            if (playerViewModel.stationProperty.value.isValid()) {
                                playerViewModel.togglePlayerState()
                            }
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
    }
}