package online.hudacek.broadcastsfx.controllers

import javafx.stage.Stage
import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.fragments.*
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import online.hudacek.broadcastsfx.model.StationViewModel
import tornadofx.Controller

class MenuBarController : Controller() {

    val currentStation: StationViewModel by inject()
    val mediaPlayer = MediaPlayerWrapper

    fun openStats() {
        find<StatsFragment>().openModal(stageStyle = StageStyle.UTILITY)
    }

    fun openStationInfo() {
        find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)
    }

    fun openAbout() {
        find<AboutAppFragment>().openModal(stageStyle = StageStyle.UTILITY)
    }

    fun openServerSelect() {
        find<ServerSelectionFragment>().openModal(stageStyle = StageStyle.UTILITY)
    }

    fun openAttributions() {
        find<AttributionsFragment>().openModal(stageStyle = StageStyle.UTILITY)
    }

    fun closeApp(currentStage: Stage?) {
        currentStage?.close()
        mediaPlayer.releasePlayer()
    }
}