package online.hudacek.broadcastsfx.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val playerBackground by cssclass()
        val grayLabel by cssclass()
        val noBorder by cssclass()
    }

    init {
        playerBackground {
            padding = box(3.px, 10.px, 3.px, 10.px)
            backgroundRadius = multi(box(3.px))
            backgroundColor = multi(Paint.valueOf("#E9E9E9"))
            borderRadius = multi(box(3.px))
            borderColor += box(c("#E8E8E8"))
        }

        grayLabel {
            textFill = Color.GRAY
            font = Font(11.0)
        }

        noBorder {
            unsafe("-fx-padding", raw("0"))
            unsafe("-fx-background-insets", raw("0"))
            unsafe("-fx-background-color", raw("-fx-background"))
            unsafe("-fx-border-color", raw("transparent"))
            unsafe("-fx-faint-focus-color", raw("transparent"))
        }
    }
}