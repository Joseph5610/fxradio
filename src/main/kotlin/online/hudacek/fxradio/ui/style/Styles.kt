/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("MagicNumber")

package online.hudacek.fxradio.ui.style

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontSmoothingType
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Type-safe CSS classes used around the app
 */
class Styles : Stylesheet() {

    private val colors = LightAppearance()

    companion object {

        val grayLabel by cssclass()

        val playerMainBox by cssclass()
        val playerStationBox by cssclass()

        val playerControls by cssclass()
        val playerControlsBorder by cssclass()

        val libraryListView by cssclass()
        val libraryListItem by cssclass()

        val decoratedListView by cssclass()
        val decoratedListItem by cssclass()
        val listItemTag by cssclass()

        val primaryButton by cssclass()
        val segmentedButton by cssclass()

        val header by cssclass()
        val subheader by cssclass()

        val tag by cssclass()
        val boldText by cssclass()

        val backgroundWhite by cssclass()
        val backgroundWhiteSmoke by cssclass()

        // For Text() elements
        val primaryTextColor by cssclass()
        val defaultTextColor by cssclass()
        val grayTextColor by cssclass()

        val noBorder by cssclass()

        val mainMenuBox by cssclass()

        val glyphIconPrimary by cssclass()
        val glyphIcon by cssclass()

        val colorRadioButton by cssclass()
    }

    init {
        root {
            focusColor = c(colors.primary)
            faintFocusColor = c("${colors.primary}22")
        }

        label {
            textFill = c(colors.label)
            fontSmoothingType = FontSmoothingType.GRAY
        }

        playerMainBox {
            padding = box(5.0.px, 35.0.px)
            borderColor += box(
                c(colors.transparent), c(colors.transparent),
                c(colors.backgroundBorder),
                c(colors.transparent)
            )
        }

        playerStationBox {
            padding = box(5.0.px)
            backgroundRadius += box(4.px)
            borderRadius += box(4.px)
            backgroundColor += c(colors.background)
            borderColor += box(c(colors.backgroundBorder))
            maxWidth = 280.px
            minWidth = 280.px
            prefWidth = 280.px
            alignment = Pos.CENTER
            prefHeight = 40.px
            minHeight = 40.px
            maxHeight = 40.px
        }

        tag {
            textFill = c(colors.label)
            padding = box(3.0.px, 10.0.px)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            backgroundColor += c(colors.background)
            borderColor += box(c(colors.backgroundBorder))
        }

        header {
            wrapText = true
            fontSize = 20.px
        }

        subheader {
            wrapText = true
            fontSize = 14.px
        }

        primaryTextColor {
            fill = c(colors.primary)
            textFill = c(colors.primary)
        }

        defaultTextColor {
            fill = c(colors.label)
        }

        grayTextColor {
            textFill = c(colors.grayLabel)
        }

        playerControls {
            unsafe("-fx-padding", raw("0"))
            unsafe("-fx-background-insets", raw("0"))
            unsafe("-fx-background-color", raw("-fx-background"))
            unsafe("-fx-border-color", raw(colors.transparent))
            unsafe("-fx-faint-focus-color", raw(colors.transparent))
        }

        playerControlsBorder {
            and(hover) {
                backgroundRadius += box(6.px)
                backgroundColor += c(colors.background)
            }
        }

        grayLabel {
            fontSize = 11.px
            textFill = c(colors.grayLabel)
        }

        button {
            baseColor = c(colors.backgroundSelected)
            minWidth = 75.px
            minHeight = 25.px
            fontSize = 12.px
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            padding = box(5.px, 10.px, 5.px, 10.px)
            textFill = c(colors.label)
        }

        primaryButton {
            baseColor = c(colors.primary)
            textFill = Color.WHITESMOKE
        }

        segmentedButton {
            minHeight = 20.px
        }

        libraryListView {
            backgroundColor += Color.WHITESMOKE
            borderColor += box(Color.WHITESMOKE)
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        libraryListItem {
            fontSize = 12.px
            prefHeight = 30.px
            backgroundColor += Color.WHITESMOKE
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            and(selected) {
                backgroundColor += c(colors.primary)
                borderColor += box(c(colors.primary + "22"))

                label and (listItemTag) {
                    textFill = c(colors.grayLabel)
                }

                menuItem {
                    label {
                        textFill = c(colors.label)
                    }
                }

                label {
                    textFill = Color.WHITESMOKE
                }
            }
            padding = box(6.px, 10.px, 6.px, 10.px)
        }

        listItemTag {
            padding = box(2.px)
            textFill = c(colors.grayLabel)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            backgroundColor += c(colors.backgroundBorder)
            borderColor += box(c(colors.backgroundBorder + "22"))
        }

        decoratedListView {
            backgroundColor += Color.WHITE
            borderColor += box(Color.WHITE)
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        decoratedListItem {
            fontSize = 12.px
            textFill = c(colors.label)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            and(odd) {
                backgroundColor += Color.WHITE
            }
            and(even) {
                backgroundColor += Color.WHITESMOKE
            }
            and(selected) {
                borderColor += box(c(colors.primary))
            }
            padding = box(10.px, 10.px, 10.px, 10.px)
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

        mainMenuBox {

        }

        // ===================================================================
        // Restyled default components
        // ===================================================================

        tableView {
            tableRowCell {
                and(selected) {
                    backgroundColor += c(colors.primary)
                }
            }
        }

        checkBox {
            box {
                baseColor = c(colors.backgroundSelected)
                backgroundRadius += box(3.px)
                borderRadius += box(3.px)
            }

            and(selected) {
                box {
                    baseColor = c(colors.primary)
                }
                mark {
                    backgroundColor += Color.WHITE
                }
            }
        }

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

        splitPaneDivider {
            padding = box(0.px, 0.001.em)
            backgroundColor += Color.TRANSPARENT
            borderColor += box(c(colors.backgroundBorder))
        }

        datagrid {
            padding = box(0.px)
        }

        datagridCell {
            backgroundColor += c(colors.transparent)
            borderColor += box(c(colors.transparent))
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)

            and(hover) {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.backgroundBorder))
            }

            and(selected) {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.primary))
            }
        }

        textArea {
            fontFamily = "monospace"
            accentColor = c(colors.primary, 0.6)
            backgroundColor += c(colors.background)
            content {
                backgroundColor += c(colors.background)
            }
        }

        slider {
            track {
                prefHeight = 5.px
            }

            thumb {
                prefHeight = 14.px
                prefWidth = 14.px
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
            borderColor += box(c(colors.backgroundBorder))
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)

            menuItem {
                backgroundRadius += box(6.px)
                borderRadius += box(6.px)

                and(hover) {
                    backgroundColor += c(colors.background)
                }
                and(focused) {
                    backgroundColor += c(colors.background)
                }
                and(selected) {
                    backgroundColor += c(colors.background)
                }
            }
        }

        menuBar {
            backgroundColor += c(colors.backgroundBorder)

            menu {
                backgroundColor += c(colors.background)

                and(hover) {
                    backgroundColor += c(colors.background)
                }
                and(focused) {
                    backgroundColor += c(colors.background)
                }
                and(selected) {
                    backgroundColor += c(colors.background)
                }
            }
        }

        textField {
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            accentColor = c(colors.primary, 0.6)
        }

        noBorder {
            backgroundInsets += box(0.px)
            padding = box(0.px)
        }

        glyphIconPrimary {
            textFill = c(colors.primary)
        }

        glyphIcon {
            textFill = c(colors.label)
        }

        comboBox {
            baseColor = c(colors.backgroundSelected)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)

            arrow {
                backgroundColor += c(colors.primary)
            }

            comboBoxPopup {
                listView {
                    backgroundRadius += box(6.px)
                    borderRadius += box(6.px)
                    backgroundColor += c(colors.backgroundBorder)
                }

                listCell {
                    textFill = c(colors.label)
                    backgroundRadius += box(6.px)
                    borderRadius += box(6.px)
                    backgroundColor += c(colors.backgroundBorder)
                    and(selected) {
                        backgroundColor += c(colors.primary)
                        borderColor += box(c(colors.primary))
                        textFill = Color.WHITESMOKE
                    }
                    padding = box(5.px)
                }
            }
        }

        InternalWindow.Styles.floatingWindowWrapper {

            InternalWindow.Styles.top {
                backgroundRadius += box(6.px, 6.px, 0.px, 0.px)
                borderRadius += box(6.px, 6.px, 0.px, 0.px)
            }

            InternalWindow.Styles.closebutton {
                visibility = FXVisibility.HIDDEN
            }

            InternalWindow.Styles.floatingWindowContent {
                backgroundRadius += box(0.px, 0.px, 6.px, 6.px)
                borderRadius += box(0.px, 0.px, 6.px, 6.px)
            }
        }
    }
}
