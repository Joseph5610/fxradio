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
import uk.co.caprica.vlcj.log.LogLevel
import uk.co.caprica.vlcj.log.NativeLog
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

private val logger = KotlinLogging.logger {}

/**
 * VLC Player Audio Component
 * Implements playing logic using VLC player
 */
class VLCAudioComponent {

    private val vlcEvents = VLCEvents()

    private lateinit var nativeLog: NativeLog

    val player: AudioPlayerComponent? by lazy {
        try {
            AudioPlayerComponent()
        } catch (e: UnsatisfiedLinkError) {
            null
        }
    }

    init {
        if (player == null) {
            throw RuntimeException("VLCLib cannot be found on the system.")
        }

        player?.let {
            it.mediaPlayer().events().addMediaPlayerEventListener(vlcEvents.mediaStatusAdapter)
            it.mediaPlayer().events().addMediaEventListener(vlcEvents.mediaMetaDataAdapter)

            nativeLog = it.mediaPlayerFactory().application().newLog().apply {
                level = LogLevel.NOTICE
                addLogListener(vlcEvents.nativeLogListener)
            }
        }

        vlcEvents.endPlayingEvent.subscribe {
            stop()
        }
    }

    fun play(url: String) {
        player?.mediaPlayer()?.media()?.play(url)
    }

    fun setVolume(volume: Int) = player?.mediaPlayer()?.audio()?.setVolume(volume) ?: false

    fun stop() {
        // Its not allowed to call back into LibVLC from an event handling thread,
        // so submit() is used
        try {
            player?.let {
                it.mediaPlayer().submit {
                    it.mediaPlayer().controls().stop()
                }
            }
        } catch (e: Exception) {
            logger.debug { "Can't stop stream" }
        }
    }

    fun release() {
        logger.info { "Releasing player" }
        player?.let {
            it.mediaPlayer().events().removeMediaEventListener(vlcEvents.mediaMetaDataAdapter)
            it.mediaPlayer().events().removeMediaPlayerEventListener(vlcEvents.mediaStatusAdapter)
            nativeLog.removeLogListener(vlcEvents.nativeLogListener)
            nativeLog.release()
            it.release()
        }
    }
}