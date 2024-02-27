/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.media

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import online.hudacek.fxradio.event.AppEvent
import online.hudacek.fxradio.event.data.AppNotification
import online.hudacek.fxradio.util.applySchedulersFlowable
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.Controller
import tornadofx.get
import tornadofx.onChange
import java.util.concurrent.TimeUnit

interface SleepTimer {
    fun onTick(tickValue: Int) {}

    fun onFinish() {}
}

/**
 * Service that will stop player automatically after specified time
 */
class SleepTimerService : Controller(), SleepTimer {

    private val appEvent = find<AppEvent>()
    private val playerViewModel: PlayerViewModel by inject()
    private var timerSubscription: Disposable? = null
    private val finishNotification =
        AppNotification(messages["player.sleepTimerFinished"], FontAwesome.Glyph.CLOCK_ALT)

    init {
        playerViewModel.sleepTimerIntervalProperty.onChange {
            timerSubscription?.dispose()
            start()
        }
    }

    fun start() {
        if (playerViewModel.sleepTimerIntervalProperty.value != 0) {
            timerSubscription?.dispose()
            timerSubscription = createCountDownFlowable().subscribe()
        }
    }

    override fun onFinish() {
        playerViewModel.stateProperty.value = PlayerState.Stopped
        appEvent.appNotification.onNext(finishNotification)
    }

    private fun createCountDownFlowable() = Flowable.range(0, playerViewModel.sleepTimerIntervalProperty.value + 1)
        .map { playerViewModel.sleepTimerIntervalProperty.value - it }
        .concatMap { Flowable.just(it).delay(1, TimeUnit.SECONDS) }
        .compose(applySchedulersFlowable())
        .doOnNext(::onTick)
        .doOnComplete(::onFinish)
}
