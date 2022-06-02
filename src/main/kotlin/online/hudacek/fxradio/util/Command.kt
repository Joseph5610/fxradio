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

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Execute OS command
 */
class Command(private val command: String) {

    fun exec() = Runtime.getRuntime().exec(command).result

    /**
     * Parse result of command to string
     */
    private val Process.result: String
        get() {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.forEachLine {
                sb.append(it)
            }
            return sb.toString()
        }
}
