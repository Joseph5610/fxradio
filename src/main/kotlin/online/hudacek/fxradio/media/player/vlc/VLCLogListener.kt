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

package online.hudacek.fxradio.media.player.vlc

import mu.KotlinLogging
import uk.co.caprica.vlcj.log.LogEventListener
import uk.co.caprica.vlcj.log.LogLevel

private val logger = KotlinLogging.logger {}

/**
 * Listen for VLC native logs and print them to our logger
 */
class VLCLogListener : LogEventListener {

    var lastLogMessage: String = ""
        private set

    override fun log(level: LogLevel?, module: String?, file: String?, line: Int?, name: String?,
                     header: String?, id: Int?, message: String) {
        lastLogMessage = message
        logger.info { "[$module] ($name) $level: $message" }
    }
}