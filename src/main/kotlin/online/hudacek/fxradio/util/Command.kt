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

package online.hudacek.fxradio.util

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Execute local command
 */
class Command(command: String) {

    private val commands = command.split(" ").toTypedArray()

    fun exec() = Runtime.getRuntime().exec(commands, null, null).also {
        logger.debug { "Executed local command: ${commands.joinToString()}" }
    }.result

    /**
     * Parse result of command to string
     */
    private val Process.result: String
        get() = StringBuilder().apply {
            inputStream.use { inputStream ->
                inputStream.bufferedReader().useLines { sequence ->
                    sequence.forEach {
                        append(it)
                    }
                }
            }
        }.toString().also {
            logger.debug { "Command Result: $it" }
        }
}
