package online.hudacek.broadcastsfx.controllers

import javafx.stage.Stage
import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.fragments.*
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import online.hudacek.broadcastsfx.ui.set
import online.hudacek.broadcastsfx.views.MainView
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class MenuBarController : Controller() {

    private val mediaPlayer: MediaPlayerWrapper by inject()

    private val notification by lazy { find(MainView::class).notification }

    fun openStats() = find<StatsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openStationInfo() = find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAbout() = find<AboutAppFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openServerSelect() = find<ServerSelectionFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAttributions() = find<AttributionsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun clearCache() {
        confirm(messages["cache.clear.confirm"], messages["cache.clear.text"]) {
            if (ImageCache.clearCache()) {
                notification[FontAwesome.Glyph.CHECK] = messages["cache.clear.ok"]
            } else {
                notification[FontAwesome.Glyph.CHECK] = messages["cache.clear.error"]
            }
        }
    }

    fun closeApp(currentStage: Stage?) {
        currentStage?.close()
        mediaPlayer.release()
    }

    fun openAddNewStation() = find<AddStationFragment>().openModal(stageStyle = StageStyle.UTILITY)
}