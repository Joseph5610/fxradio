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

package online.hudacek.fxradio.media.player.humble

import io.humble.video.Decoder
import io.humble.video.Demuxer
import io.humble.video.MediaAudio
import io.humble.video.MediaDescriptor
import online.hudacek.fxradio.media.StreamUnavailableException

internal fun Decoder.getMediaAudio() = MediaAudio.make(frameSize, sampleRate, channels, channelLayout, sampleFormat)

/**
 * Try to get basic stream information from [streamUrl]
 * Returns opened decoder or null when error happened
 */
internal fun Demuxer.getStreamInfo(streamUrl: String): HumbleStreamInfo? {
    try {
        open(streamUrl, null, false, true, null, null)
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
