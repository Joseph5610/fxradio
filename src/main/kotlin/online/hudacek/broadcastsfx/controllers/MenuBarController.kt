package online.hudacek.broadcastsfx.controllers

import javafx.stage.Stage
import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.fragments.*
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.Controller

class MenuBarController : Controller() {

    val mediaPlayer: MediaPlayerWrapper by inject()

    fun openStats() = find<StatsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openStationInfo() = find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAbout() = find<AboutAppFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openServerSelect() = find<ServerSelectionFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAttributions() = find<AttributionsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun clearCache() = ImageCache.clearCache()

    fun closeApp(currentStage: Stage?) {
        currentStage?.close()
        mediaPlayer.release()
    }
}