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

package online.hudacek.fxradio.media

import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.value

/**
 * Common interface for all available players
 */
interface MediaPlayer {

    /**
     * Supported types of player
     */
    enum class Type {
        Humble, VLC
    }

    val playerType: Type

    /**
     * Starts playing stream with URL [streamUrl]
     */
    fun play(streamUrl: String)

    /**
     * Changes playing value to [newVolume]
     */
    fun changeVolume(newVolume: Double)

    fun stop()

    fun release()

    companion object {
        //The metadata service can be disabled by respective property file setting
        val isMetaDataRefreshEnabled = Properties.PlayerMetaDataRefresh.value(true)
    }
}