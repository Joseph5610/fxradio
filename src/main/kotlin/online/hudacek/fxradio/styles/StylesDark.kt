package online.hudacek.fxradio.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontSmoothingType
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Type-safe CSS classes used around the app
 */
class StylesDark : Stylesheet() {

    private val colors = DarkColorValues()

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
            textFill = c(colors.label)
            fontSmoothingType = FontSmoothingType.GRAY
        }

        searchBoxLabel {
            padding = box(0.px, 2.px, 0.px, 7.px)
        }

        textInput {
            backgroundColor += c(colors.backgroundBorder)
            textFill = Color.WHITESMOKE
        }

        playerMainBox {
            padding = box(10.0.px, 0.0.px)
            borderColor += box(c(colors.transparent), c(colors.transparent), c(colors.backgroundBorder), c(colors.transparent))
        }

        playerStationBox {
            padding = box(3.0.px, 10.0.px)
            backgroundRadius += box(3.px)
            borderRadius += box(3.px)
            backgroundColor += c(colors.background)
            borderColor += box(c(colors.backgroundBorder))
            maxWidth = 260.px
            prefWidth = 260.px
        }

        tag {
            textFill = Color.BLACK
            padding = box(3.0.px, 10.0.px)
            backgroundColor += c(colors.background)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            borderColor += box(c(colors.backgroundBorder))

            and(hover) {
                textFill = Color.WHITESMOKE
            }
        }

        header {
            textFill = c(colors.label)
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
            unsafe("-fx-background-color", raw(colors.background))
            unsafe("-fx-border-color", raw("transparent"))
            unsafe("-fx-faint-focus-color", raw("transparent"))
        }

        grayLabel {
            fontSize = 11.px
            textFill = c(colors.grayLabel)
        }

        coloredButton {
            backgroundColor += c(colors.background)
            borderColor += box(c(colors.backgroundBorder))
            textFill = Color.WHITESMOKE

            and(hover) {
                backgroundColor += c(colors.backgroundSelected)
            }
            and(focused) {
                backgroundColor += c(colors.backgroundSelected)
            }
            and(selected) {
                backgroundColor += c(colors.backgroundSelected)
            }
        }

        primaryButton {
            backgroundColor += c(colors.primary)
            textFill = Color.WHITESMOKE
            and(hover) {
                backgroundColor += c(colors.primaryHover)
            }
        }

        primaryTextColor {
            textFill = c(colors.primary)
        }

        libraryListView {
            backgroundColor += c("#333232")
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        libraryListItem {
            fontSize = 12.px
            textFill = c(colors.label)
            backgroundColor += c("#333232")
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            and(selected) {
                backgroundColor += c(colors.backgroundBorder)
                borderColor += box(c(colors.backgroundBorder))
                textFill = Color.WHITESMOKE
            }
            padding = box(5.px, 10.px, 5.px, 15.px)
        }

        libraryListItemTag {
            padding = box(2.px)
            textFill = Color.WHITESMOKE
            borderColor += box(c(colors.backgroundBorder))
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
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
            backgroundColor += c(colors.transparent)
            borderColor += box(c(colors.transparent))

            incrementButton {
                backgroundColor += c(colors.transparent)
                backgroundRadius += box(0.em)
                padding = box(0.px, 10.px, 0.px, 0.px)
            }

            decrementButton {
                backgroundColor += c(colors.transparent)
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
                backgroundColor += c(colors.transparent)
                borderColor += box(c(colors.transparent))
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
            borderColor += box(c(colors.backgroundBorder))
        }

        datagrid {
            padding = box(0.px)
        }

        datagridCell {
            padding = box(0.px, 5.px, 5.px, 5.px)
            backgroundColor += c("#6e6e6e")
            borderColor += box(c("#6e6e6e"))
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)

            and(hover) {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.backgroundBorder))
            }

            and(selected) {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.backgroundBorder))
            }
        }

        textArea {
            fontFamily = "monospace"
        }

        slider {
            track {
                prefHeight = 3.px
                backgroundColor += c(colors.backgroundBorder)
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
            backgroundColor += c(colors.background)
            borderColor += box(c(colors.backgroundBorder))
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)

            menuItem {
                backgroundRadius += box(6.px)
                borderRadius += box(6.px)

                and(hover) {
                    backgroundColor += c(colors.backgroundSelected)
                }
                and(focused) {
                    backgroundColor += c(colors.backgroundSelected)
                }
                and(selected) {
                    backgroundColor += c(colors.backgroundSelected)
                }
            }
        }

        menuBar {
            backgroundColor += c(colors.backgroundBorder)

            menu {
                backgroundColor += c(colors.background)

                and(hover) {
                    backgroundColor += c(colors.backgroundSelected)
                }
                and(focused) {
                    backgroundColor += c(colors.backgroundSelected)
                }
                and(selected) {
                    backgroundColor += c(colors.backgroundSelected)
                }
            }
        }

        progressIndicator {
            progressColor = c(colors.grayLabel)
        }

        statusBar {
            padding = box(5.px)
            borderRadius += box(0.px)
            borderWidth += box(1.px, 0.px, 0.px, 0.px)
            borderColor += box(c(colors.backgroundBorder), c(colors.transparent), c(colors.transparent), c(colors.transparent))
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        textField {
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
        }
    }
}