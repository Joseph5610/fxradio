package online.hudacek.broadcastsfx.media

import kotlinx.coroutines.CoroutineScope
import online.hudacek.broadcastsfx.events.PlayingStatus

interface MediaPlayer {

    var playingStatus: PlayingStatus

    fun play(scope: CoroutineScope, url: String)
    fun changeVolume(volume: Float): Boolean
}