package online.hudacek.broadcastsfx.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val playerStationInfo by cssclass()
        val grayLabel by cssclass()
        val noBorder by cssclass()
        val primaryButton by cssclass()

        const val primaryColor = "#0097CE"
        const val hoverColor = "#0097EA"
    }

    init {
        playerStationInfo {
            padding = box(3.px, 10.px, 3.px, 10.px)
            backgroundRadius = multi(box(3.px))
            backgroundColor = multi(Paint.valueOf("#E9E9E9"))
            borderRadius = multi(box(3.px))
            borderColor += box(c("#E8E8E8"))
            maxWidth = 260.px
        }

        grayLabel {
            fontSize = 11.px
            textFill = Color.GRAY
        }

        primaryButton {
            backgroundColor += c(primaryColor)
            textFill = Color.WHITESMOKE
            and(hover) {
                backgroundColor += c(hoverColor)
            }
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