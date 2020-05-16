package online.hudacek.broadcastsfx.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

/**
 * Type-safe CSS classes used around the app
 */
class Styles : Stylesheet() {
    companion object {
        val playerStationInfo by cssclass()
        val grayLabel by cssclass()
        val libraryListView by cssclass()
        val primaryButton by cssclass()
        val playerControls by cssclass()
        val header by cssclass()
        val subheader by cssclass()
        val tag by cssclass()
        val searchBoxLabel by cssclass()
        val primaryTextColor by cssclass()
        val customListItem by cssclass()

        private const val primaryColor = "#0097CE"
        private const val hoverColor = "#0097EA"

        val colorPrimary = Color.valueOf(primaryColor)
    }

    init {
        searchBoxLabel {
            padding = box(0.px, 2.px, 0.px, 7.px)
        }

        playerStationInfo {
            padding = box(3.0.px, 10.0.px)
            backgroundRadius += box(3.px)
            borderRadius += box(3.px)
            backgroundColor += Paint.valueOf("#E9E9E9")
            borderColor += box(c("#E8E8E8"))
            maxWidth = 260.px
            prefWidth = 260.px
        }

        tag {
            padding = box(3.0.px, 10.0.px)
            backgroundRadius += box(3.px)
            backgroundColor += Paint.valueOf("#E9E9E9")
            borderRadius += box(3.px)
            borderColor += box(c("#E8E8E8"))

            and(hover) {
                textFill = Color.BLACK
            }
        }

        header {
            wrapText = true
            fontSize = 20.px
        }

        subheader {
            wrapText = true
            fontSize = 16.px
        }

        playerControls {
            unsafe("-fx-padding", raw("0"))
            unsafe("-fx-background-insets", raw("0"))
            unsafe("-fx-background-color", raw("-fx-background"))
            unsafe("-fx-border-color", raw("transparent"))
            unsafe("-fx-faint-focus-color", raw("transparent"))
        }

        grayLabel {
            fontSize = 11.px
            textFill = Color.GRAY
        }

        splitPaneDivider {
            padding = box(0.px, 0.01.em)
            borderColor += box(c("transparent"))
        }

        primaryButton {
            backgroundColor += c(primaryColor)
            textFill = Color.WHITESMOKE
            and(hover) {
                backgroundColor += c(hoverColor)
            }
        }

        primaryTextColor {
            textFill = Paint.valueOf(primaryColor)
        }

        libraryListView {
            backgroundColor += Color.WHITESMOKE
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        customListItem {
            textFill = Color.BLACK
            backgroundColor += Color.WHITESMOKE
            and(hover) {
                backgroundColor += Paint.valueOf("#E9E9E9")
                borderColor += box(c("#E8E8E8"))
                textFill = Color.BLACK
            }
            and(selected) {
                backgroundColor += Paint.valueOf("#E9E9E9")
                borderColor += box(c("#E8E8E8"))
                textFill = Color.BLACK
                graphicContainer {
                    backgroundColor += Color.BLACK
                }
            }
        }


        scrollBar {
            backgroundColor += Paint.valueOf("transparent")
            borderColor += box(c("transparent"))

            incrementButton {
                backgroundColor += Paint.valueOf("transparent")
                backgroundRadius += box(0.em)
                padding = box(0.px, 10.px, 0.px, 0.px)
            }

            decrementButton {
                backgroundColor += Paint.valueOf("transparent")
                backgroundRadius += box(0.em)
                padding = box(0.px, 10.px, 0.px, 0.px)
            }

            incrementArrow {
                shape = " "
                padding = box(0.px, 0.08.em)
            }

            decrementArrow {
                shape = " "
                padding = box(0.px, 0.08.em)
            }

            track {
                backgroundColor += Paint.valueOf("transparent")
                borderColor += box(c("transparent"))
                unsafe("-fx-background-radius", raw("0.0em"))
                unsafe("-fx-border-radius", raw("2.0em"))
            }

            thumb {
                unsafe("-fx-background-color", raw("derive(black,90.0%)"))
                unsafe("-fx-background-insets", raw("2.0, 0.0, 0.0"))
                unsafe("-fx-background-radius", raw("2.0em"))

                and(hover) {
                    unsafe("-fx-background-color", raw("derive(#4D4C4F,10.0%)"))
                    unsafe("-fx-background-insets", raw("2.0, 0.0, 0.0"))
                    unsafe("-fx-background-radius", raw("2.0em"))
                }
            }
        }

        datagrid {
            padding = box(0.px)
        }

        datagridCell {
            padding = box(0.px, 5.px, 5.px, 5.px)
            backgroundColor += Paint.valueOf("transparent")
            borderColor += box(c("transparent"))
            backgroundRadius += box(5.px)
            borderRadius += box(5.px)

            and(hover) {
                backgroundColor += Paint.valueOf("#E9E9E9")
                borderColor += box(c("#E8E8E8"))
            }

            and(selected) {
                backgroundColor += Paint.valueOf("#E9E9E9")
                borderColor += box(c("#E8E8E8"))
            }
        }
    }
}