package online.hudacek.broadcastsfx.styles

import tornadofx.*

class NoBorder : Stylesheet() {
    companion object {
        val style by cssclass()
    }

    init {
        style {
            unsafe("-fx-padding", raw("0"))
            unsafe("-fx-background-insets", raw("0"))
            unsafe("-fx-background-color", raw("-fx-background"))
            unsafe("-fx-border-color", raw("transparent"))
            unsafe("-fx-faint-focus-color", raw("transparent"))
        }
    }
}