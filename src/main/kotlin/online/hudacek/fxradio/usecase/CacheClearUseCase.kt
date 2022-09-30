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

package online.hudacek.fxradio.usecase

import com.github.thomasnield.rxkotlinfx.toMaybe
import io.reactivex.Maybe
import io.reactivex.Single
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import mu.KotlinLogging
import online.hudacek.fxradio.persistence.cache.ImageCache
import online.hudacek.fxradio.ui.formatted
import online.hudacek.fxradio.util.confirmDialog
import tornadofx.get

private val logger = KotlinLogging.logger {}

/**
 * Clears image cache directory
 */
class CacheClearUseCase : BaseUseCase<Unit, Maybe<Boolean>>() {

    private val alert: Alert = confirmDialog(messages["cache.clear.confirm"],
            messages["cache.clear.text"].formatted(ImageCache.totalSize), owner = primaryStage)

    override fun execute(input: Unit): Maybe<Boolean> = alert.toMaybe()
            .defaultIfEmpty(ButtonType.CANCEL)
            .filter { it == ButtonType.OK }
            .flatMapSingleElement { Single.just(ImageCache.clear()) }
            .doOnError { logger.error(it) { "Exception when deleting cache!" } }
}
