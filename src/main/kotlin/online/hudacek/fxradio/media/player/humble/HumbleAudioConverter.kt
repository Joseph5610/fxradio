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

private const val JAVA_SAMPLE_RATE = 44100

class HumbleAudioConverter(
    private val mMediaSampleRate: Int,
    private val mMediaLayout: AudioChannel.Layout,
    private val mMediaFormat: AudioFormat.Type
) : MediaAudioConverter {

    private val mJavaChannels: Int
    private val mJavaLayout = AudioChannel.Layout.CH_LAYOUT_STEREO
    private val mJavaFormat = AudioFormat.Type.SAMPLE_FMT_S16
    private val mMediaChannels: Int

    private val mMediaAudioToJavaSoundResampler: MediaAudioResampler?
    private var mResampledAudio: MediaAudio? = null

    init {
        require(mMediaSampleRate >= 0) { "sample rate must be > 0" }
        require(mMediaLayout != AudioChannel.Layout.CH_LAYOUT_UNKNOWN) { "channel layout must be known" }
        mJavaChannels = AudioChannel.getNumChannelsInLayout(mJavaLayout)
        mMediaChannels = AudioChannel.getNumChannelsInLayout(mMediaLayout)
        if (willResample()) {
            mMediaAudioToJavaSoundResampler = MediaAudioResampler.make(
                mJavaLayout,
                JAVA_SAMPLE_RATE, mJavaFormat, mMediaLayout, mMediaSampleRate, mMediaFormat
            )
            mMediaAudioToJavaSoundResampler.open()
        } else {
            mMediaAudioToJavaSoundResampler = null
        }
    }

    override fun getJavaFormat(): javax.sound.sampled.AudioFormat {
        return javax.sound.sampled.AudioFormat(
            JAVA_SAMPLE_RATE.toFloat(),
            AudioFormat.getBytesPerSample(mJavaFormat) * 8, mJavaChannels, true, false
        )
    }

    override fun getMediaSampleRate() = mMediaSampleRate

    override fun getMediaChannels() = mMediaChannels

    override fun getMediaLayout() = mMediaLayout

    override fun getMediaFormat() = mMediaFormat

    private fun willResample() =
        !(mMediaSampleRate == JAVA_SAMPLE_RATE && mMediaChannels == mJavaChannels && mMediaFormat == mJavaFormat)

    private fun validateMediaAudio(audio: MediaAudio?) {
        requireNotNull(audio) { "must pass in audio" }
        require(audio.sampleRate == mMediaSampleRate) { "input sample rate does not match value converter expected" }
        require(audio.channelLayout == mMediaLayout) { "input channel layout does not match value converter expected" }
        require(audio.format == mMediaFormat) { "input sample format does not match value converter expected" }
        require(audio.isComplete) { "input audio is not complete" }
    }

    override fun toJavaAudio(output: ByteBuffer?, input: MediaAudio): ByteBuffer {
        var outputBuffer = output
        validateMediaAudio(input)
        val audio: MediaAudio?
        val outputNumSamples: Int
        if (willResample()) {
            outputNumSamples = mMediaAudioToJavaSoundResampler!!.getNumResampledSamples(input.numSamples)
            if (mResampledAudio == null ||
                mResampledAudio!!.maxNumSamples < outputNumSamples
            ) {
                if (mResampledAudio != null) mResampledAudio!!.delete()
                mResampledAudio = MediaAudio.make(
                    outputNumSamples,
                    JAVA_SAMPLE_RATE, mJavaChannels, mJavaLayout, mJavaFormat
                )
            }
            audio = mResampledAudio
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
        if (willResample()) {
            mMediaAudioToJavaSoundResampler!!.resample(audio, input)
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
