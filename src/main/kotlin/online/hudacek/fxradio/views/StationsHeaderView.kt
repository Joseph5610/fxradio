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

package online.hudacek.fxradio.views

import javafx.geometry.Pos
import mu.KotlinLogging
import online.hudacek.fxradio.storage.Database
import online.hudacek.fxradio.events.LibraryRefreshConditionalEvent
import online.hudacek.fxradio.events.LibraryRefreshEvent
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.styles.Styles
import tornadofx.*

/**
 * Bar with opened library name and action button within stationsView
 */
class StationsHeaderView : View() {

    private val logger = KotlinLogging.logger {}

    private val actionButton = button {
        text = messages["favourites.clean"]
        action {
            favouritesCleanAction()
        }
        hide()
    }

    private val shownLibraryName = label {
        paddingTop = 8.0
        paddingBottom = 8.0
        addClass(Styles.subheader)
    }

    init {
        subscribe<LibraryRefreshEvent> {
            applyActionButton(it.type)
            shownLibraryName.text = when (it.type) {
                LibraryType.Favourites -> messages["favourites"]
                LibraryType.History -> messages["history"]
                LibraryType.TopStations -> messages["topStations"]
                LibraryType.Search -> messages["searchResultsFor"] + " \"${it.params}\""
                else -> it.params
            }
        }
    }

    override val root = borderpane {
        padding = insets(horizontal = 10.0, vertical = 0.0)
        maxHeight = 10.0
        addClass(Styles.backgroundWhiteSmoke)
        left {
            add(shownLibraryName)
        }

        right {
            vbox(alignment = Pos.CENTER_RIGHT) {
                add(actionButton)
            }
        }
    }

    private fun applyActionButton(type: LibraryType) {
        if (type == LibraryType.Favourites) {
            actionButton.show()
        } else {
            actionButton.hide()
        }
    }

    fun hide() = root.hide()
    fun show() = root.show()

    private fun favouritesCleanAction() {
        confirm(messages["database.clear.confirm"], messages["database.clear.text"]) {
            Database
                    .cleanup()
                    .subscribe({
                        fire(NotificationEvent(messages["database.clear.ok"]))
                        fire(LibraryRefreshConditionalEvent(LibraryType.Favourites))
                    }, {
                        logger.error(it) { "Can't remove favourites!" }
                        fire(NotificationEvent(messages["database.clear.error"]))
                    })
        }
    }
}
