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

package online.hudacek.fxradio.ui.viewmodel

import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import online.hudacek.fxradio.events.AppEvent
import tornadofx.ItemViewModel
import tornadofx.property

class SelectedLibrary(type: LibraryType = LibraryType.TopStations,
                      libraryOption: String = "") {
    var type: LibraryType by property(type)
    var libraryOption: String by property(libraryOption)
}

class SelectedLibraryViewModel : ItemViewModel<SelectedLibrary>(SelectedLibrary()) {
    private val appEvent: AppEvent by inject()

    val typeProperty = bind(SelectedLibrary::type) as ObjectProperty
    val libraryOption = bind(SelectedLibrary::libraryOption) as StringProperty

    init {
        appEvent.refreshLibrary
                .filter { typeProperty.value == it }
                .subscribe {
                    item = SelectedLibrary(it)
                }
    }
}