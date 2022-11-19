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

import javafx.beans.property.StringProperty
import tornadofx.property

data class License(val name: String = "", val content: String)

class Attribution(name: String, version: String = "", license: License) {
    var name: String by property(name)
    var version: String by property(version)
    var licenseContent: String by property(license.content)
    var licenseName: String by property(license.name)
}

/**
 * Attribution view model
 * -------------------
 * Handles information about Licensing
 * Used in [online.hudacek.fxradio.ui.fragment.AttributionsFragment]
 */
class AttributionViewModel : BaseViewModel<Attribution>() {
    val nameProperty = bind(Attribution::name) as StringProperty
    val licenseNameProperty = bind(Attribution::licenseName) as StringProperty
    val licenseContentProperty = bind(Attribution::licenseContent) as StringProperty
}
