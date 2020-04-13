package online.hudacek.broadcastsfx.extension

import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import kotlin.system.exitProcess



val mediaPlayerComponent = AudioPlayerComponent()

fun main2() {
    println("aaa")
    mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
        override fun finished(mediaPlayer: MediaPlayer?) {
            end(0)
        }

        override fun error(mediaPlayer: MediaPlayer?) {
            end(1)
        }
    })
    mediaPlayerComponent.mediaPlayer().media().play(
            "http://masima.rastream.com/masima-pramborsjakarta?awparams=stationid:masima-pramborsjakarta"
    )
    try {
        Thread.currentThread().join()
    } catch (e: InterruptedException) {
    }
}

fun main() {
    online.hudacek.broadcastsfx.MediaPlayer.playSound("http://masima.rastream.com/masima-pramborsjakarta?awparams=stationid:masima-pramborsjakarta")
}

fun end(result: Int) { // It is not allowed to call back into LibVLC from an event handling thread, so submit() is used
   println("end")
    mediaPlayerComponent.mediaPlayer().submit(Runnable {
        mediaPlayerComponent.mediaPlayer().release()
        exitProcess(result);
    })
}