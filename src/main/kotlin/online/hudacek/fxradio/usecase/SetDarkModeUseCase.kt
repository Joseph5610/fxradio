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

package online.hudacek.fxradio.usecase

import javafx.beans.property.BooleanProperty
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.style.StylesDark
import tornadofx.FX
import tornadofx.importStylesheet
import tornadofx.removeStylesheet

class SetDarkModeUseCase : BaseUseCase<BooleanProperty, Unit>() {

    override fun execute(input: BooleanProperty) {
        removeStylesheet(Styles::class)
        removeStylesheet(StylesDark::class)
        if (input.value) {
            importStylesheet(StylesDark::class)
        } else {
            importStylesheet(Styles::class)
        }
        FX.applyStylesheetsTo(FX.primaryStage.scene)
    }
}