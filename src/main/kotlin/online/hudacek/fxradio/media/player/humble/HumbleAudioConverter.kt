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

import io.humble.video.AudioChannel
import io.humble.video.AudioFormat
import io.humble.video.MediaAudio
import io.humble.video.MediaAudioResampler
import io.humble.video.javaxsound.MediaAudioConverter
import java.nio.ByteBuffer

class HumbleAudioConverter(private val mMediaSampleRate: Int,
                           private val mMediaLayout: AudioChannel.Layout,
                           private val mMediaFormat: AudioFormat.Type) : MediaAudioConverter {

    private val mJavaSampleRate = 44100
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
            mMediaAudioToJavaSoundResampler = MediaAudioResampler.make(mJavaLayout,
                    mJavaSampleRate, mJavaFormat, mMediaLayout, mMediaSampleRate, mMediaFormat)
            mMediaAudioToJavaSoundResampler.open()
        } else {
            mMediaAudioToJavaSoundResampler = null
        }
    }

    override fun getJavaFormat(): javax.sound.sampled.AudioFormat {
        return javax.sound.sampled.AudioFormat(mJavaSampleRate.toFloat(),
                AudioFormat.getBytesPerSample(mJavaFormat) * 8, mJavaChannels, true, false)
    }

    override fun getMediaSampleRate() = mMediaSampleRate

    override fun getMediaChannels() = mMediaChannels

    override fun getMediaLayout() = mMediaLayout

    override fun getMediaFormat() = mMediaFormat

    private fun willResample() = !(mMediaSampleRate == mJavaSampleRate && mMediaChannels == mJavaChannels && mMediaFormat == mJavaFormat)

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
                    mResampledAudio!!.maxNumSamples < outputNumSamples) {
                if (mResampledAudio != null) mResampledAudio!!.delete()
                mResampledAudio = MediaAudio.make(outputNumSamples,
                        mJavaSampleRate, mJavaChannels, mJavaLayout, mJavaFormat)
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