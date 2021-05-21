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
                playerViewModel.mediaPlayerProperty.value =
                        MediaPlayerFactory.toggle(playerViewModel.mediaPlayerProperty.value.playerType)
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

                visibleWhen {
                    //Notifications available only on Mac
                    MacUtils.isMac.toProperty()
                }
            }
    )
}
