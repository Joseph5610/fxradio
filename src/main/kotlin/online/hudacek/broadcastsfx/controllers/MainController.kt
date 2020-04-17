package online.hudacek.broadcastsfx.controllers

import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.extension.MediaPlayerWrapper
import online.hudacek.broadcastsfx.fragments.AboutAppFragment
import online.hudacek.broadcastsfx.fragments.StationInfoFragment
import tornadofx.Controller

class MainController : Controller() {

    private val mediaPlayer = MediaPlayerWrapper

    fun openStationInfo() {
        find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)
    }

    fun openAbout() {
        find<AboutAppFragment>().openModal(stageStyle = StageStyle.UTILITY)
    }

    fun cancelMediaPlaying() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.cancelPlaying()
        }
    }
}