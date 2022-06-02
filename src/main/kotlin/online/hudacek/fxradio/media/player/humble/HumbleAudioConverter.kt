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

import io.humble.video.AudioChannel
import io.humble.video.AudioFormat
import io.humble.video.MediaAudio
import io.humble.video.MediaAudioResampler
import io.humble.video.javaxsound.MediaAudioConverter
import java.nio.ByteBuffer

private val javaLayout = AudioChannel.Layout.CH_LAYOUT_STEREO
private val javaAudioFormat = AudioFormat.Type.SAMPLE_FMT_S16
private val javaChannels: Int by lazy { AudioChannel.getNumChannelsInLayout(javaLayout) }

private const val javaSampleRate = 44100

class HumbleAudioConverter(mediaAudio: MediaAudio) : MediaAudioConverter {

    private val mediaSampleRate: Int = mediaAudio.sampleRate
    private val mediaLayout: AudioChannel.Layout = mediaAudio.channelLayout
    private val mediaFormat: AudioFormat.Type = mediaAudio.format
    private val mediaChannels: Int = AudioChannel.getNumChannelsInLayout(mediaLayout)

    private val resample by lazy {
        (mediaSampleRate == javaSampleRate && mediaChannels == javaChannels && mediaFormat == javaAudioFormat)
    }

    private val mediaAudioToJavaSoundResampler: MediaAudioResampler?

    private var resampledAudio: MediaAudio? = null

    init {
        require(mediaSampleRate >= 0) { "sample rate must be > 0" }
        require(mediaLayout != AudioChannel.Layout.CH_LAYOUT_UNKNOWN) { "channel layout must be known" }

        mediaAudioToJavaSoundResampler = if (resample) {
            MediaAudioResampler.make(javaLayout,
                    javaSampleRate, javaAudioFormat, mediaLayout, mediaSampleRate, mediaFormat)
        } else {
            null
        }

        mediaAudioToJavaSoundResampler?.open()
    }

    override fun getJavaFormat(): javax.sound.sampled.AudioFormat {
        return javax.sound.sampled.AudioFormat(javaSampleRate.toFloat(),
                AudioFormat.getBytesPerSample(javaAudioFormat) * 8, javaChannels, true, false)
    }

    override fun getMediaSampleRate() = mediaSampleRate

    override fun getMediaChannels() = mediaChannels

    override fun getMediaLayout() = mediaLayout

    override fun getMediaFormat() = mediaFormat

    private fun validateMediaAudio(audio: MediaAudio) {
        require(audio.sampleRate == mediaSampleRate) { "input sample rate does not match value converter expected" }
        require(audio.channelLayout == mediaLayout) { "input channel layout does not match value converter expected" }
        require(audio.format == mediaFormat) { "input sample format does not match value converter expected" }
        require(audio.isComplete) { "input audio is not complete" }
    }

    override fun toJavaAudio(output: ByteBuffer?, input: MediaAudio): ByteBuffer {
        var outputBuffer = output
        validateMediaAudio(input)
        val audio: MediaAudio?
        val outputNumSamples: Int
        if (resample) {
            outputNumSamples = mediaAudioToJavaSoundResampler!!.getNumResampledSamples(input.numSamples)
            if (resampledAudio == null ||
                    resampledAudio!!.maxNumSamples < outputNumSamples) {
                if (resampledAudio != null) resampledAudio!!.delete()
                resampledAudio = MediaAudio.make(outputNumSamples,
                        javaSampleRate, javaChannels, javaLayout, javaAudioFormat)
            }
            audio = resampledAudio
        } else {
            outputNumSamples = input.numSamples
            audio = input
        }
        val size = AudioFormat.getBufferSizeNeeded(outputNumSamples, audio!!.channels, audio.format)
        if (outputBuffer == null) {
            outputBuffer = ByteBuffer.allocate(size)
        } else {
            if (outputBuffer.capacity() < size) throw RuntimeException("output bytes not large enough to hold data")
        }
        if (resample) {
            mediaAudioToJavaSoundResampler!!.resample(audio, input)
        }
        // now, copy the resulting data into the bytes.
        // we force audio to be packed, so only one plane.
        val buffer = audio.getData(0)
        val bufferSize = audio.getDataPlaneSize(0)
        val bytes = outputBuffer!!.array()
        buffer[0, bytes, 0, bufferSize]
        outputBuffer.limit(size)
        outputBuffer.position(0)
        buffer.delete()
        return outputBuffer
    }
}