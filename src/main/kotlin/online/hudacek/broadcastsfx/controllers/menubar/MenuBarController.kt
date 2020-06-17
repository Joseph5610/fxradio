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

package online.hudacek.broadcastsfx.controllers.menubar

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javafx.stage.Stage
import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.FxRadio
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.extension.openUrl
import online.hudacek.broadcastsfx.fragments.*
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import online.hudacek.broadcastsfx.model.PlayerModel
import online.hudacek.broadcastsfx.views.menubar.MenuBarView
import tornadofx.*

class MenuBarController : Controller() {

    private val stationsApi: StationsApi
        get() = StationsApi.client

    private val menuBarView: MenuBarView by inject()

    private val mediaPlayerWrapper: MediaPlayerWrapper by inject()
    private val playerModel: PlayerModel by inject()

    fun openStats() = find<StatsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openStationInfo() = find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openServerSelect() = find<ChangeServerFragment>().openModal(stageStyle = StageStyle.UTILITY, resizable = false)

    fun openAttributions() = find<AttributionsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAbout() = find<AboutFragment>().openModal(stageStyle = StageStyle.UTILITY, resizable = false)

    fun clearCache() = ImageCache.clearCache()

    fun closeApp(currentStage: Stage?) {
        currentStage?.close()
        mediaPlayerWrapper.release()
    }

    fun openAddNewStation() = find<AddStationFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun voteForStation(): Disposable = stationsApi
            .vote(playerModel.stationProperty.value.stationuuid)
            .observeOnFx()
            .subscribeOn(Schedulers.io())
            .subscribe({
                menuBarView.showVoteResult(it.ok)
            }, {
                menuBarView.showVoteResult(false)
            })

    fun openWebsite() = app.openUrl(FxRadio.appUrl)
}