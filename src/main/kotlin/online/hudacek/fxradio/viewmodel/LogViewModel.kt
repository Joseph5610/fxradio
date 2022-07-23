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

package online.hudacek.fxradio.viewmodel

import javafx.beans.property.ObjectProperty
import online.hudacek.fxradio.util.Properties
import online.hudacek.fxradio.util.save
import online.hudacek.fxradio.util.value
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import tornadofx.property

class Log(level: Level = Level.valueOf(Properties.LogLevel.value("INFO"))) {
    var level: Level by property(level)
}

/**
 * Keeps information about current logging level chosen in UI
 * Used in [online.hudacek.fxradio.ui.view.menu.MenuBarView]
 */
class LogViewModel : BaseViewModel<Log>(Log()) {
    val levelProperty = bind(Log::level) as ObjectProperty

    init {
        //Init correct log level after view model creation
        setLogLevel()
    }

    override fun onCommit() {
        //Set Current Logger Level
        setLogLevel()

        //Save it
        Properties.LogLevel.save(levelProperty.value)
    }

    private fun setLogLevel() = Configurator.setAllLevels(LogManager.getRootLogger().name, levelProperty.value)
}
