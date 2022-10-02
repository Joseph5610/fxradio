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

package online.hudacek.fxradio.ui.fragment

import javafx.geometry.Pos
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.requestFocusOnSceneAvailable
import online.hudacek.fxradio.ui.smallLabel
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.SelectedStation
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.bind
import tornadofx.button
import tornadofx.get
import tornadofx.paddingAll
import tornadofx.paddingBottom
import tornadofx.stringProperty
import tornadofx.textfield
import tornadofx.vbox
import java.util.*

/***
 * Simple Information about the app
 */
class OpenStreamFragment : BaseFragment(FxRadio.appName) {

    private val selectedStationViewModel: SelectedStationViewModel by inject()

    private val streamUrlProperty = stringProperty()

    override val root = vbox {
        paddingAll = 15.0
        prefWidth = 300.0
        title = messages["menu.stream.title"]

        vbox(alignment = Pos.CENTER) {
            paddingBottom = 10.0
            smallLabel(messages["menu.stream.url"])
            textfield {
                requestFocusOnSceneAvailable()
                bind(streamUrlProperty)
            }
        }

        vbox(alignment = Pos.CENTER_RIGHT) {
            button(messages["open"]) {
                action {
                    if (streamUrlProperty.value != null) {
                        selectedStationViewModel.item = SelectedStation(
                                createStreamStation()
                        )
                        close()
                    }
                }
                addClass(Styles.primaryButton)
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }

    /**
     * Create adhoc Station object containing entered URL address
     */
    private fun createStreamStation() = Station(UUID.randomUUID().toString(), "Stream URL",
            streamUrlProperty.value, streamUrlProperty.value, null, tags = streamUrlProperty.value)

}
