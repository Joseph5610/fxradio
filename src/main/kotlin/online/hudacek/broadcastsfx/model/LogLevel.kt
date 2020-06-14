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

package online.hudacek.broadcastsfx.model

import javafx.beans.property.ObjectProperty
import online.hudacek.broadcastsfx.Config
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import tornadofx.*

class LogLevel(level: Level) {
    var level: Level by property(level)
}

class LogLevelModel : ItemViewModel<LogLevel>() {
    val level = bind(LogLevel::level) as ObjectProperty

    override fun onCommit() {
        super.onCommit()

        //Set Current Logger Level
        Configurator.setAllLevels(LogManager.getRootLogger().name, level.value)

        //Save it
        with(app.config) {
            set(Config.Keys.logLevel to level.value)
            save()
        }
    }
}