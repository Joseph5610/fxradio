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

package online.hudacek.fxradio.media.player.vlc

import mu.KotlinLogging
import uk.co.caprica.vlcj.log.LogEventListener
import uk.co.caprica.vlcj.log.LogLevel

private val logger = KotlinLogging.logger {}

/**
 * Listen for VLC native logs and print them to our logger
 */
class VLCLogListener : LogEventListener {

    override fun log(
        level: LogLevel?, module: String?, file: String?, line: Int?, name: String?,
        header: String?, id: Int?, message: String
    ) {
        logger.debug { "[$module] ($name) $level: $message" }
    }
}
