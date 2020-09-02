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

package online.hudacek.fxradio.extension

import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import online.hudacek.fxradio.api.model.Station
import tornadofx.*

/**
 * This is to overcome a bug that sometimes
 * scene is not available when requesting focus
 */
internal fun Node.requestFocusOnSceneAvailable() = if (scene == null) {
    val listener = object : ChangeListener<Scene> {
        override fun changed(observable: ObservableValue<out Scene>?, oldValue: Scene?, newValue: Scene?) {
            if (newValue != null) {
                sceneProperty().removeListener(this)
                requestFocus()
            }
        }
    }
    sceneProperty().addListener(listener)
} else {
    requestFocus()
}

internal fun Node.shouldBeDisabled(station: Property<Station>) {
    disableWhen(booleanBinding(station) {
        value == null || !value.isValidStation()
    })
}