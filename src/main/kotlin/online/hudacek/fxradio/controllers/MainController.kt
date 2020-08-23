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

package online.hudacek.fxradio.controllers

import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.media.MediaPlayerWrapper
import online.hudacek.fxradio.viewmodel.LogLevel
import online.hudacek.fxradio.viewmodel.LogLevelModel
import org.apache.logging.log4j.Level
import tornadofx.*

class MainController : Controller() {

    private val logLevel: LogLevelModel by inject()
    private val mediaPlayerWrapper: MediaPlayerWrapper by inject()


    private val logger = KotlinLogging.logger {}

    fun cancelMediaPlaying() = mediaPlayerWrapper.release()

    fun appInit() {
        //init logger
        val savedLevel = Level.valueOf(app.config.string(Config.Keys.logLevel))
        logLevel.item = LogLevel(savedLevel)
        logLevel.commit()
        logger.debug { "App init called." }
    }
}