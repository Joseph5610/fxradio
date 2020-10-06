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

package online.hudacek.fxradio.views.menu

import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.scene.control.CheckMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.events.RefreshFavourites
import online.hudacek.fxradio.utils.*
import online.hudacek.fxradio.viewmodel.*
import org.apache.logging.log4j.Level
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

//TODO remove
object Menus : Component() {

    private val menuViewModel: MenuViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()
    private val libraryViewModel: LibraryViewModel by inject()
    private val stationsHistoryViewModel: StationsHistoryViewModel by inject()
    private val logViewModel: LogViewModel by inject()

    private val keyInfo = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)
    private val keyAdd = KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)
    private val keyFavourites = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)

    private var checkLoggerOff: CheckMenuItem by singleAssign()
    private var checkLoggerInfo: CheckMenuItem by singleAssign()
    private var checkLoggerAll: CheckMenuItem by singleAssign()

    init {
        stationsHistoryViewModel.item = StationsHistoryModel()
        logViewModel.levelProperty.onChange { it?.let(Menus::updateSelectedLoggerLevel) }
    }

    val historyMenu = menu(messages["menu.history"]) {
        item(messages["menu.history.show"]).action {
            libraryViewModel.selectedProperty.value = SelectedLibrary(LibraryType.History)
        }
        separator()
        menu(messages["menu.history.recent"]) {
            disableWhen {
                stationsHistoryViewModel.stationsProperty.emptyProperty()
            }
            items.bind(stationsHistoryViewModel.stationsProperty) {
                item("${it.name} (${it.countrycode})") {
                    //for some reason macos native menu does not respect
                    //width/height setting so it is disabled for now
                    if (!menuViewModel.useNative) {
                        graphic = imageview {
                            createImage(it)
                            fitHeight = 15.0
                            fitWidth = 15.0
                            isPreserveRatio = true
                        }
                    }
                    action {
                        playerViewModel.stationProperty.value = it
                    }
                }
            }
        }
        separator()
        item(messages["menu.history.clear"]).action {
            stationsHistoryViewModel.item = StationsHistoryModel()
        }
    }

    val favouritesMenu = menu(messages["menu.favourites"]) {
        item(messages["menu.favourites.show"]).action {
            libraryViewModel.selectedProperty.value = SelectedLibrary(LibraryType.Favourites)
        }
        separator()
        item(messages["menu.station.favourite"], keyFavourites) {
            disableWhen(playerViewModel.stationProperty.booleanBinding {
                it == null || !it.isValid() || it.isFavourite.blockingGet()
            })

            actionEvents()
                    .flatMapSingle { playerViewModel.stationProperty.value.isFavourite }
                    .filter { !it }
                    .flatMapSingle { playerViewModel.stationProperty.value.addFavourite() }
                    .subscribe({
                        fire(NotificationEvent(messages["menu.station.favourite.added"], FontAwesome.Glyph.CHECK))
                    }, {
                        fire(NotificationEvent(messages["menu.station.favourite.error"]))
                    })
        }

        item(messages["menu.station.favourite.remove"]) {
            visibleWhen(playerViewModel.stationProperty.booleanBinding {
                it != null && it.isValid() && it.isFavourite.blockingGet()
            })

            actionEvents()
                    .flatMapSingle { playerViewModel.stationProperty.value.isFavourite }
                    .filter { it }
                    .flatMapSingle { playerViewModel.stationProperty.value.removeFavourite() }
                    .subscribe({
                        fire(NotificationEvent(messages["menu.station.favourite.removed"], FontAwesome.Glyph.CHECK))
                        fire(RefreshFavourites())
                    }, {
                        fire(NotificationEvent(messages["menu.station.favourite.remove.error"]))
                    })
        }
    }

    val stationMenu = menu(messages["menu.station"]) {
        item(messages["menu.station.info"], keyInfo) {
            shouldBeDisabled(playerViewModel.stationProperty)
            action {
                menuViewModel.openStationInfo()
            }
        }
        item(messages["menu.station.vote"]) {
            shouldBeDisabled(playerViewModel.stationProperty)
            action {
                menuViewModel.handleVote()
            }
        }

        item(messages["copy.stream.url"]) {
            action {
                playerViewModel.stationProperty.value.url_resolved?.let { clipboard.update(it) }
            }

            enableWhen {
                playerViewModel.stationProperty.booleanBinding {
                    it != null && it.url_resolved != null
                }
            }
        }

        separator()
        item(messages["menu.station.add"], keyAdd) {
            isVisible = Config.Flags.addStationEnabled
            action {
                menuViewModel.openAddNewStation()
            }
        }
    }

    val helpMenu = menu(messages["menu.help"]) {
        item(messages["menu.help.stats"]).action {
            menuViewModel.openStats()
        }
        item(messages["menu.help.clearCache"]).action {
            confirm(messages["cache.clear.confirm"], messages["cache.clear.text"]) {
                menuViewModel.clearCache()
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
                menuViewModel.openWebsite()
            }
        }
        separator()
        item(messages["menu.help.vcs.check"]) {
            action {
                VersionCheck.perform()
            }
        }
        separator()
        menu(messages["menu.help.loglevel"]) {
            checkLoggerOff = checkmenuitem(messages["menu.help.loglevel.off"]) {
                isSelected = logViewModel.levelProperty.value == Level.OFF
                action {
                    saveNewLogger(Level.OFF)
                }
            }
            checkLoggerInfo = checkmenuitem(messages["menu.help.loglevel.info"]) {
                isSelected = logViewModel.levelProperty.value == Level.INFO
                action {
                    saveNewLogger(Level.INFO)
                }
            }
            checkLoggerAll = checkmenuitem(messages["menu.help.loglevel.debug"]) {
                isSelected = logViewModel.levelProperty.value == Level.DEBUG
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
        logViewModel.levelProperty.value = level
        logViewModel.commit()
    }

    private fun updateSelectedLoggerLevel(level: Level) {
        checkLoggerOff.isSelected = level == Level.OFF
        checkLoggerInfo.isSelected = level == Level.INFO
        checkLoggerAll.isSelected = level == Level.ALL
    }
}