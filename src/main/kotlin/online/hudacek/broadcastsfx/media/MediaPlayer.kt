package online.hudacek.broadcastsfx.media

interface MediaPlayer {
    fun play(url: String)
    fun changeVolume(volume: Double): Boolean
    fun cancelPlaying()
    fun releasePlayer()
}