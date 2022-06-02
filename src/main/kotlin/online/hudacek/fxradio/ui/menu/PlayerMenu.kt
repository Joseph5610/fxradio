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

import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.MediaPlayerFactory
import online.hudacek.fxradio.util.macos.MacUtils
import online.hudacek.fxradio.viewmodel.OsNotificationViewModel
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class PlayerMenu : BaseMenu("menu.player.controls") {

    private val playerViewModel: PlayerViewModel by inject()
    private val osNotificationViewModel: OsNotificationViewModel by inject()

    private val playerTypeItem by lazy {
        checkMenuItem(messages["menu.player.switch"]) {
            isSelected = playerViewModel.mediaPlayerProperty.value?.playerType == MediaPlayer.Type.Humble
            action {
                playerViewModel.stateProperty.value = PlayerState.Stopped
                playerViewModel.mediaPlayerProperty.value?.release()
                playerViewModel.mediaPlayerProperty.value = MediaPlayerFactory.toggle()
                playerViewModel.commit()
            }
        }
    }

    init {
        playerViewModel.mediaPlayerProperty.onChange {
            playerTypeItem.isSelected = it?.playerType == MediaPlayer.Type.Humble
        }
    }

    override val menuItems = listOf(
            item(messages["menu.player.start"], KeyCodes.play) {
                disableWhenInvalidStation(playerViewModel.stationProperty)
                action {
                    playerViewModel.stateProperty.value = PlayerState.Playing
                }
            },
            item(messages["menu.player.stop"], KeyCodes.stop) {
                disableWhenInvalidStation(playerViewModel.stationProperty)
                action {
                    playerViewModel.stateProperty.value = PlayerState.Stopped
                }
            },
            separator(),
            playerTypeItem,

            checkMenuItem(messages["menu.player.animate"],
                    bindProperty = playerViewModel.animateProperty) {
                action {
                    playerViewModel.commit()
                }
            },

            checkMenuItem(messages["menu.player.notifications"],
                    bindProperty = osNotificationViewModel.showProperty) {
                action {
                    osNotificationViewModel.commit()
                }
            }
    )
}
