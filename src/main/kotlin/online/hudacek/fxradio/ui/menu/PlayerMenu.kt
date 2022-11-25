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

package online.hudacek.fxradio.ui.menu

import javafx.scene.control.CheckMenuItem
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.MediaPlayerFactory
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.action
import tornadofx.booleanBinding
import tornadofx.get
import tornadofx.onChange

class PlayerMenu : BaseMenu("menu.player.controls") {

    private val playerViewModel: PlayerViewModel by inject()
    private val selectedStationViewModel: SelectedStationViewModel by inject()

    private val startItem by lazy {
        item(messages["menu.player.start"], KeyCodes.play) {
            disableWhenInvalidStation(selectedStationViewModel.stationProperty)
            action {
                playerViewModel.stateProperty.value = selectedStationViewModel.streamUrlProperty.let {
                    PlayerState.Playing(it.value)
                }
            }
        }
    }

    private val stopItem by lazy {
        item(messages["menu.player.stop"], KeyCodes.stop) {
            disableWhenInvalidStation(selectedStationViewModel.stationProperty)
            action {
                playerViewModel.stateProperty.value = PlayerState.Stopped
            }
        }
    }

    private val playerTypeItem: CheckMenuItem by lazy {
        checkMenuItem(messages["menu.player.switch"]) {
            isSelected = playerShowProperty.value
            action {
                with(playerViewModel) {
                    stateProperty.value = PlayerState.Stopped
                    mediaPlayerProperty.value?.release()
                    mediaPlayerProperty.value = MediaPlayerFactory.toggle()
                    commit()
                }
            }
        }
    }

    private val playerShowProperty = playerViewModel.mediaPlayerProperty.booleanBinding {
        it?.playerType == MediaPlayer.Type.Humble
    }.onChange {
        playerTypeItem.isSelected = it
    }

    private val animateItem by lazy {
        checkMenuItem(messages["menu.player.animate"], bindProperty = playerViewModel.animateProperty) {
            action {
                playerViewModel.commit()
            }
        }
    }

    override val menuItems = listOf(startItem, stopItem, separator(), playerTypeItem, animateItem)
}
