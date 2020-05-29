package online.hudacek.broadcastsfx.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Type-safe CSS classes used around the app
 */
class Styles : Stylesheet() {

    private object ColorValues {
        const val primary = "#0097CE"
        const val primaryHover = "#0097EA"

        const val background = "#E9E9E9"
        const val backgroundBorder = "#E8E8E8"

        const val transparent = "transparent"
    }

    companion object {

        val grayLabel by cssclass()

        val playerMainBox by cssclass()
        val playerStationBox by cssclass()
        val playerControls by cssclass()

        val libraryListView by cssclass()
        val libraryListItem by cssclass()

        val primaryButton by cssclass()

        val header by cssclass()
        val subheader by cssclass()

        val tag by cssclass()
        val searchBoxLabel by cssclass()
        val primaryTextColor by cssclass()
        val boldText by cssclass()

        val backgroundWhite by cssclass()
        val backgroundWhiteSmoke by cssclass()

        val colorPrimary: Color = Color.valueOf(ColorValues.primary)
    }

    init {
        searchBoxLabel {
            padding = box(0.px, 2.px, 0.px, 7.px)
        }

        playerMainBox {
            padding = box(10.0.px, 0.0.px)
            borderColor += box(c(ColorValues.transparent), c(ColorValues.transparent), c(ColorValues.backgroundBorder), c(ColorValues.transparent))
        }

        playerStationBox {
            padding = box(3.0.px, 10.0.px)
            backgroundRadius += box(3.px)
            borderRadius += box(3.px)
            backgroundColor += c(ColorValues.background)
            borderColor += box(c(ColorValues.backgroundBorder))
            maxWidth = 260.px
            prefWidth = 260.px
        }

        tag {
            textFill = Color.BLACK
            padding = box(3.0.px, 10.0.px)
            backgroundRadius += box(3.px)
            backgroundColor += c(ColorValues.background)
            borderRadius += box(3.px)
            borderColor += box(c(ColorValues.backgroundBorder))

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
            borderColor += box(c(ColorValues.transparent))
        }

        primaryButton {
            backgroundColor += c(ColorValues.primary)
            textFill = Color.WHITESMOKE
            and(hover) {
                backgroundColor += c(ColorValues.primaryHover)
            }
        }

        primaryTextColor {
            textFill = c(ColorValues.primary)
        }

        libraryListView {
            backgroundColor += Color.WHITESMOKE
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        libraryListItem {
            textFill = Color.BLACK
            backgroundColor += Color.WHITESMOKE
            and(hover) {
                backgroundColor += c(ColorValues.background)
                borderColor += box(c(ColorValues.backgroundBorder))
                textFill = Color.BLACK
            }
            and(selected) {
                backgroundColor += c(ColorValues.background)
                borderColor += box(c(ColorValues.backgroundBorder))
                textFill = Color.BLACK
                graphicContainer {
                    backgroundColor += Color.BLACK
                }
            }
        }

        boldText {
            fontWeight = FontWeight.BOLD
        }

        backgroundWhiteSmoke {
            backgroundColor += Color.WHITESMOKE
        }

        backgroundWhite {
            backgroundColor += Color.WHITE
        }

        // ===================================================================
        // Restyled default compontents
        // ===================================================================

        scrollBar {
            backgroundColor += c(ColorValues.transparent)
            borderColor += box(c(ColorValues.transparent))

            incrementButton {
                backgroundColor += c(ColorValues.transparent)
                backgroundRadius += box(0.em)
                padding = box(0.px, 10.px, 0.px, 0.px)
            }

            decrementButton {
                backgroundColor += c(ColorValues.transparent)
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
                backgroundColor += c(ColorValues.transparent)
                borderColor += box(c(ColorValues.transparent))
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
            backgroundColor += c(ColorValues.transparent)
            borderColor += box(c(ColorValues.transparent))
            backgroundRadius += box(5.px)
            borderRadius += box(5.px)

            and(hover) {
                backgroundColor += c(ColorValues.background)
                borderColor += box(c(ColorValues.backgroundBorder))
            }

            and(selected) {
                backgroundColor += c(ColorValues.background)
                borderColor += box(c(ColorValues.backgroundBorder))
            }
        }

        textArea {
            fontFamily = "monospace"
        }
    }
}