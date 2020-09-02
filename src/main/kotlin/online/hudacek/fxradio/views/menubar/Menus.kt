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

package online.hudacek.fxradio.views.menubar

import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.scene.control.CheckMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.controllers.menubar.MenuBarController
import online.hudacek.fxradio.events.LibraryRefreshConditionalEvent
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.extension.createImage
import online.hudacek.fxradio.extension.menu
import online.hudacek.fxradio.extension.openUrl
import online.hudacek.fxradio.extension.shouldBeDisabled
import online.hudacek.fxradio.viewmodel.LogLevelModel
import online.hudacek.fxradio.viewmodel.PlayerModel
import online.hudacek.fxradio.viewmodel.StationsHistoryModel
import org.apache.logging.log4j.Level
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.tools.Platform
import tornadofx.*

object Menus : Component() {

    private val controller: MenuBarController by inject()
    private val playerModel: PlayerModel by inject()
    private val stationsHistory: StationsHistoryModel by inject()
    private val logLevel: LogLevelModel by inject()

    private val keyInfo = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)
    private val keyAdd = KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)
    private val keyFavourites = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)

    private var checkLoggerOff: CheckMenuItem by singleAssign()
    private var checkLoggerInfo: CheckMenuItem by singleAssign()
    private var checkLoggerAll: CheckMenuItem by singleAssign()

    private val usePlatformMenuBarProperty = app.config.boolean(Config.Keys.useNativeMenuBar, true)

    init {
        logLevel.level.onChange { it?.let(::updateSelectedLoggerLevel) }
    }

    val historyMenu = menu(messages["menu.history"]) {
        shouldBeDisabled(playerModel.stationProperty)
        items.bind(stationsHistory.stations) {
            item("${it.name} (${it.countrycode})") {
                //for some reason macos native menu does not respect
                //width/height setting so it is disabled for now
                if (Platform.getCurrent() != Platform.OSX || !usePlatformMenuBarProperty) {
                    graphic = imageview {
                        createImage(it)
                        fitHeight = 15.0
                        fitWidth = 15.0
                        isPreserveRatio = true
                    }
                }
                action {
                    playerModel.stationProperty.value = it
                }
            }
        }
    }

    val stationMenu = menu(messages["menu.station"]) {
        item(messages["menu.station.info"], keyInfo) {
            shouldBeDisabled(playerModel.stationProperty)
            action {
                controller.openStationInfo()
            }
        }

        item(messages["menu.station.favourite"], keyFavourites) {
            disableWhen(booleanBinding(playerModel.stationProperty) {
                value == null || !value.isValidStation() || value.isFavourite.blockingGet()
            })

            actionEvents()
                    .flatMapSingle { playerModel.stationProperty.value.isFavourite }
                    .filter { !it }
                    .flatMapSingle { playerModel.stationProperty.value.addFavourite() }
                    .subscribe({
                        fire(NotificationEvent(messages["menu.station.favourite.added"], FontAwesome.Glyph.CHECK))
                    }, {
                        fire(NotificationEvent(messages["menu.station.favourite.error"]))
                    })
        }

        item(messages["menu.station.favourite.remove"]) {
            visibleWhen(booleanBinding(playerModel.stationProperty) {
                value != null && value.isValidStation() && value.isFavourite.blockingGet()
            })

            actionEvents()
                    .flatMapSingle { playerModel.stationProperty.value.isFavourite }
                    .filter { it }
                    .flatMapSingle { playerModel.stationProperty.value.removeFavourite() }
                    .subscribe({
                        fire(NotificationEvent(messages["menu.station.favourite.removed"], FontAwesome.Glyph.CHECK))
                        fire(LibraryRefreshConditionalEvent(LibraryType.Favourites))
                    }, {
                        fire(NotificationEvent(messages["menu.station.favourite.remove.error"]))
                    })
        }

        item(messages["menu.station.vote"]) {
            shouldBeDisabled(playerModel.stationProperty)
            action {
                controller.voteForStation()
            }
        }

        item(messages["menu.station.add"], keyAdd) {
            isVisible = Config.Flags.addStationEnabled
            action {
                controller.openAddNewStation()
            }
        }
    }

    val helpMenu = menu(messages["menu.help"]) {
        item(messages["menu.help.stats"]).action {
            controller.openStats()
        }
        item(messages["menu.help.clearCache"]).action {
            confirm(messages["cache.clear.confirm"], messages["cache.clear.text"]) {
                if (controller.clearCache()) {
                    fire(NotificationEvent(messages["cache.clear.ok"], FontAwesome.Glyph.CHECK))
                } else {
                    fire(NotificationEvent(messages["cache.clear.error"]))
                }
            }
        }
        separator()
        item(messages["menu.help.openhomepage"]) {
            graphic = imageview(Config.Resources.appWebsiteIcon) {
                fitHeight = 15.0
                fitWidth = 15.0
                isPreserveRatio = true
            }
            action {
                controller.openWebsite()
            }
        }
        separator()
        item(messages["menu.help.vcs.check"]) {
            action {
                controller.checkForUpdate()
            }
        }
        separator()
        menu(messages["menu.help.loglevel"]) {
            checkLoggerOff = checkmenuitem(messages["menu.help.loglevel.off"]) {
                isSelected = logLevel.level.value == Level.OFF
                action {
                    saveNewLogger(Level.OFF)
                }
            }
            checkLoggerInfo = checkmenuitem(messages["menu.help.loglevel.info"]) {
                isSelected = logLevel.level.value == Level.INFO
                action {
                    saveNewLogger(Level.INFO)
                }
            }
            checkLoggerAll = checkmenuitem(messages["menu.help.loglevel.debug"]) {
                isSelected = logLevel.level.value == Level.DEBUG
                action {
                    saveNewLogger(Level.ALL)
                }
            }
        }
        item(messages["menu.help.logs"]).action {
            app.openUrl("file://${Config.Paths.baseAppPath}")
        }
    }

    private fun saveNewLogger(level: Level) {
        updateSelectedLoggerLevel(level)
        logLevel.level.value = level
        logLevel.commit()
    }

    private fun updateSelectedLoggerLevel(level: Level) {
        checkLoggerOff.isSelected = level == Level.OFF
        checkLoggerInfo.isSelected = level == Level.INFO
        checkLoggerAll.isSelected = level == Level.ALL
    }
}