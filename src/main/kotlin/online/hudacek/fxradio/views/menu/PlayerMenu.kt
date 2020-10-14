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

package online.hudacek.fxradio.views.menu

import javafx.scene.control.CheckMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.events.PlaybackChangeEvent
import online.hudacek.fxradio.events.PlayingStatus
import online.hudacek.fxradio.media.PlayerType
import online.hudacek.fxradio.utils.menu
import online.hudacek.fxradio.utils.shouldBeDisabled
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class PlayerMenu : Controller() {

    private val playerViewModel: PlayerViewModel by inject()

    private var playerCheck: CheckMenuItem by singleAssign()

    private val keyPlay = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
    private val keyStop = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)

    init {
        playerViewModel.playerTypeProperty.onChange {
            playerCheck.isSelected = it == PlayerType.Custom
        }
    }

    val menu by lazy {
        menu(messages["menu.player.controls"]) {
            item(messages["menu.player.start"], keyPlay) {
                shouldBeDisabled(playerViewModel.stationProperty)
                action {
                    fire(PlaybackChangeEvent(PlayingStatus.Playing))
                }
            }

            item(messages["menu.player.stop"], keyStop) {
                shouldBeDisabled(playerViewModel.stationProperty)
                action {
                    fire(PlaybackChangeEvent(PlayingStatus.Stopped))
                }
            }

            separator()

            playerCheck = checkmenuitem(messages["menu.player.switch"]) {
                action {
                    fire(PlaybackChangeEvent(PlayingStatus.Stopped))
                    playerViewModel.playerTypeProperty.value =
                            if (playerViewModel.playerTypeProperty.value == PlayerType.Custom) {
                                PlayerType.VLC
                            } else {
                                PlayerType.Custom
                            }
                    playerViewModel.commit()
                }
            }

            checkmenuitem(messages["menu.player.animate"]) {
                bind(playerViewModel.animateProperty)
                action {
                    playerViewModel.commit()
                }
            }

            checkmenuitem(messages["menu.player.notifications"]) {
                bind(playerViewModel.notificationsProperty)
                action {
                    playerViewModel.commit()
                }
            }
        }
    }
}
