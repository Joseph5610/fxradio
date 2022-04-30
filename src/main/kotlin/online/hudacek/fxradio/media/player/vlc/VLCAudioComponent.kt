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
import online.hudacek.fxradio.media.AudioComponent
import online.hudacek.fxradio.media.StreamUnavailableException
import uk.co.caprica.vlcj.log.LogEventListener
import uk.co.caprica.vlcj.log.LogLevel
import uk.co.caprica.vlcj.log.NativeLog
import uk.co.caprica.vlcj.media.MediaEventListener
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

private val logger = KotlinLogging.logger {}

/**
 * VLC Player Audio Component
 * Implements playing logic using VLC player
 */
class VLCAudioComponent : AudioComponent {

    private val player: AudioPlayerComponent? by lazy { runCatching { AudioPlayerComponent() }.getOrNull() }

    private lateinit var nativeLog: NativeLog

    init {
        if (player == null) {
            throw RuntimeException("VLC player cannot be found on the system.")
        }
    }

    override fun play(streamUrl: String) {
        player?.mediaPlayer()?.media()?.play(streamUrl)
    }

    override fun setVolume(newVolume: Double) {
        player?.mediaPlayer()?.audio()?.setVolume(newVolume.toInt())
    }

    override fun cancel() {
        runCatching {
            player?.let {
                // Its not allowed to call back into LibVLC from an event handling thread,
                // so submit() is used
                it.mediaPlayer().submit {
                    it.mediaPlayer().controls().stop()
                }
            }
        }.onFailure { logger.error(it) { "Can't cancel playing..." } }
    }

    fun attachMediaListener(listener: MediaEventListener) = player?.mediaPlayer()?.events()?.addMediaEventListener(listener)

    fun removeMediaListener(listener: MediaEventListener) = player?.mediaPlayer()?.events()?.removeMediaEventListener(listener)

    fun attachLogListener(listener: LogEventListener, level: LogLevel = LogLevel.NOTICE) = player?.let {
        nativeLog = it.mediaPlayerFactory().application().newLog().apply {
            this.level = level
            addLogListener(listener)
        }
    }

    fun releaseLogListener(listener: LogEventListener) = player?.let {
        nativeLog.removeLogListener(listener)
        nativeLog.release()
    }

    fun release() = player?.release()
}