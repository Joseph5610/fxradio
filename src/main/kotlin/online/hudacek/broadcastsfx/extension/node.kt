package online.hudacek.broadcastsfx.extension

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Scene

fun Node.requestFocusOnSceneAvailable() {
    if (scene == null) {
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
}