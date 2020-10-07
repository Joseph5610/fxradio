package online.hudacek.fxradio.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontSmoothingType
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Type-safe CSS classes used around the app
 */
class StylesDark : Stylesheet() {

    private object ColorValues {
        const val primary = "#0097CE"
        const val primaryHover = "#0097EA"

        const val background = "#333232"
        const val backgroundBorder = "#525356"

        const val label = "#ffffff"
        const val grayLabel = "#a0a1a2"

        const val transparent = "transparent"
    }

    companion object {

        val grayLabel by cssclass()

        val playerMainBox by cssclass()
        val playerStationBox by cssclass()

        val playerControls by cssclass()

        val libraryListView by cssclass()
        val libraryListItem by cssclass()
        val libraryListItemTag by cssclass()

        val primaryButton by cssclass()
        val coloredButton by cssclass()

        val header by cssclass()
        val subheader by cssclass()

        val tag by cssclass()
        val searchBoxLabel by cssclass()
        val primaryTextColor by cssclass()
        val boldText by cssclass()

        val backgroundWhite by cssclass()
        val backgroundWhiteSmoke by cssclass()
        val statusBar by cssclass()

        //for Text()
        val defaultTextColor by cssclass()
    }

    init {
        label {
            textFill = c(ColorValues.label)
            fontSmoothingType = FontSmoothingType.GRAY
        }

        searchBoxLabel {
            padding = box(0.px, 2.px, 0.px, 7.px)
        }

        textInput {
            backgroundColor += c(ColorValues.backgroundBorder)
            textFill = Color.WHITESMOKE
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
                textFill = Color.WHITESMOKE
            }
        }

        header {
            textFill = c(ColorValues.label)
            wrapText = true
            fontSize = 20.px
        }

        subheader {
            wrapText = true
            fontSize = 16.px
        }

        defaultTextColor {
            fill = Color.WHITESMOKE
        }

        playerControls {
            unsafe("-fx-padding", raw("0"))
            unsafe("-fx-background-insets", raw("0"))
            unsafe("-fx-background-color", raw(ColorValues.background))
            unsafe("-fx-border-color", raw("transparent"))
            unsafe("-fx-faint-focus-color", raw("transparent"))
        }

        grayLabel {
            fontSize = 11.px
            textFill = c(ColorValues.grayLabel)
        }

        coloredButton {
            backgroundColor += c(ColorValues.backgroundBorder)
            textFill = Color.WHITESMOKE
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
            backgroundColor += c("#333232")
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        libraryListItem {
            fontSize = 12.px
            textFill = c(ColorValues.label)
            backgroundColor += c("#333232")
            and(hover) {
                backgroundColor += c(ColorValues.backgroundBorder)
                borderColor += box(c(ColorValues.backgroundBorder))
                textFill = Color.WHITESMOKE
            }
            and(selected) {
                backgroundColor += c(ColorValues.backgroundBorder)
                borderColor += box(c(ColorValues.backgroundBorder))
                textFill = Color.WHITESMOKE
            }
            padding = box(5.px, 10.px, 5.px, 15.px)
        }

        libraryListItemTag {
            padding = box(2.px)
            textFill = Color.WHITESMOKE
            backgroundRadius += box(3.px)
            backgroundColor += c("#424346")
            borderRadius += box(3.px)
            borderColor += box(c(ColorValues.backgroundBorder))
        }

        boldText {
            fontWeight = FontWeight.BOLD
        }

        backgroundWhiteSmoke {
            backgroundColor += c("#333232")
        }

        backgroundWhite {
            backgroundColor += c("#262625")
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
                unsafe("-fx-background-color", raw("derive(#9c9d9e,90.0%)"))
                unsafe("-fx-background-insets", raw("2.0, 0.0, 0.0"))
                unsafe("-fx-background-radius", raw("2.0em"))

                and(hover) {
                    unsafe("-fx-background-color", raw("derive(#4D4C4F,10.0%)"))
                    unsafe("-fx-background-insets", raw("2.0, 0.0, 0.0"))
                    unsafe("-fx-background-radius", raw("2.0em"))
                }
            }
        }

        splitPaneDivider {
            padding = box(0.px, 0.001.em)
            backgroundColor += Color.TRANSPARENT
            borderColor += box(c(ColorValues.backgroundBorder))
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

        slider {
            track {
                prefHeight = 5.px
            }
            and(focused) {
                thumb {
                    unsafe("-fx-background-color", raw("-fx-outer-border, -fx-inner-border, -fx-body-color"))
                }
            }
            and(selected) {
                thumb {
                    unsafe("-fx-color", raw("lightgray"))
                }
            }

            and(hover) {
                thumb {
                    unsafe("-fx-color", raw("lightgray"))
                }
            }
        }

        contextMenu {
            backgroundColor += Color.WHITESMOKE
            borderColor += box(c(ColorValues.backgroundBorder))

            menuItem {
                and(hover) {
                    backgroundColor += c(ColorValues.background)
                }
                and(focused) {
                    backgroundColor += c(ColorValues.background)
                }
                and(selected) {
                    backgroundColor += c(ColorValues.background)
                }
            }
        }

        menuBar {
            backgroundColor += c(ColorValues.backgroundBorder)

            menu {
                backgroundColor += c(ColorValues.background)

                and(hover) {
                    backgroundColor += c(ColorValues.background)
                }
                and(focused) {
                    backgroundColor += c(ColorValues.background)
                }
                and(selected) {
                    backgroundColor += c(ColorValues.background)
                }
            }
        }

        progressIndicator {
            progressColor = c(ColorValues.grayLabel)
        }

        statusBar {
            padding = box(5.px)
            borderRadius += box(0.px)
            borderWidth += box(1.px, 0.px, 0.px, 0.px)
            borderColor += box(c(ColorValues.backgroundBorder), c(ColorValues.transparent), c(ColorValues.transparent), c(ColorValues.transparent))
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }
    }
}