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

import javafx.scene.layout.Priority
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import tornadofx.addClass
import tornadofx.fieldset
import tornadofx.form
import tornadofx.textarea
import tornadofx.vbox
import tornadofx.vgrow

class DebugFragment : BaseFragment("Debug Window") {

    private val viewModel: SelectedStationViewModel by inject()

    override val root = vbox {
        setPrefSize(600.0, 400.0)
        form {
            fieldset("Station") {
                textarea(viewModel.stationProperty.asString()) {
                    vgrow = Priority.ALWAYS
                    isWrapText = true
                }
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}
