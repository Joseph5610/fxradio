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

package online.hudacek.fxradio.media.player.humble

import io.humble.video.Decoder
import io.humble.video.Demuxer
import io.humble.video.MediaAudio
import io.humble.video.MediaDescriptor
import online.hudacek.fxradio.media.StreamUnavailableException

internal fun Decoder.getSamples() = MediaAudio.make(
        frameSize,
        sampleRate,
        channels,
        channelLayout,
        sampleFormat)

/**
 * Try to get basic stream information from [streamUrl]
 * Returns opened decoder or null when error happened
 */
internal fun Demuxer.getStreamInfo(streamUrl: String): HumbleStreamInfo? {
    try {
        open(streamUrl, null, false, true,
                null, null)
        for (i in 0 until numStreams) {
            val stream = getStream(i)
            val decoder = stream.decoder
            if (decoder != null && decoder.codecType == MediaDescriptor.Type.MEDIA_AUDIO) {
                decoder.open(null, null)
                return HumbleStreamInfo(i, decoder)
            }
        }
    } catch (e: Exception) {
        throw StreamUnavailableException(e.localizedMessage)
    }
    return null
}