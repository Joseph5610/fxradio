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

package online.hudacek.fxradio.media.player.vlc

import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.PlayerType

/**
 * Custom Audio player using VLClib
 * Requires VLC player installed on client system
 */
class VLCPlayerImpl(override val playerType: PlayerType = PlayerType.VLC) : MediaPlayer {

    private val vlcAudioComponent = VLCAudioComponent()

    override fun play(streamUrl: String) = vlcAudioComponent.play(streamUrl)

    override fun changeVolume(newVolume: Double): Boolean {
        val vlcVolume: Int =
                if (newVolume < -29.5) {
                    0
                } else {
                    ((newVolume + 65) * (100 / 95)).toInt()
                }
        return vlcAudioComponent.setVolume(vlcVolume)
    }

    override fun stop() = vlcAudioComponent.stop()

    override fun release() = vlcAudioComponent.release()
}