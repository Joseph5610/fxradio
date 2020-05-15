package online.hudacek.broadcastsfx.controllers

import com.sun.javafx.PlatformUtil
import javafx.stage.Stage
import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.fragments.*
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.*

class MenuBarController : Controller() {

    private val mediaPlayer: MediaPlayerWrapper by inject()

    val usePlatformMenuBarProperty = app.config.boolean(Config.Keys.useNativeMenuBar, true)

    val shouldUsePlatformMenuBar = PlatformUtil.isMac() && usePlatformMenuBarProperty

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

    fun openAddNewStation() = find<AddStationFragment>().openModal(stageStyle = StageStyle.UTILITY)
}