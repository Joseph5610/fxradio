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

package online.hudacek.broadcastsfx.media

interface MediaPlayer {
    fun play(url: String)
    fun changeVolume(volume: Double): Boolean
    fun cancelPlaying()
    fun releasePlayer()

    companion object {
        val stub = object : MediaPlayer {
            override fun play(url: String) {}

            override fun changeVolume(volume: Double) = false

            override fun cancelPlaying() {}

            override fun releasePlayer() {}
        }
    }
}