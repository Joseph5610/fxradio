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

import mu.KotlinLogging
import online.hudacek.fxradio.media.MediaPlayer

private val logger = KotlinLogging.logger {}

/**
 * Custom Audio player using VLClib
 * Requires VLC player installed on client system
 */
class VLCPlayerImpl(override val playerType: MediaPlayer.Type = MediaPlayer.Type.VLC) : MediaPlayer {

    private val vlcMediaAdapter = VLCMediaAdapter()
    private val vlcAudioComponent = VLCAudioComponent()
    private val vlcLogListener = VLCLogListener()

    init {
        vlcAudioComponent.attachLogListener(vlcLogListener)

        if (MediaPlayer.isMetaDataRefreshEnabled) {
            vlcAudioComponent.attachMediaListener(vlcMediaAdapter)
        }
    }

    override fun play(streamUrl: String) = vlcAudioComponent.play(streamUrl)

    override fun changeVolume(newVolume: Double) {
        val vlcVolume: Double =
                if (newVolume < -29.5) {
                    0.0
                } else {
                    (newVolume + 65) * (100 / 95)
                }
        vlcAudioComponent.setVolume(vlcVolume)
    }

    override fun stop() = vlcAudioComponent.cancel()

    override fun release() {
        logger.info { "Releasing VLC player..." }
        vlcAudioComponent.releaseLogListener(vlcLogListener)

        if (MediaPlayer.isMetaDataRefreshEnabled) {
            vlcAudioComponent.removeMediaListener(vlcMediaAdapter)
        }
        vlcAudioComponent.release()
    }
}