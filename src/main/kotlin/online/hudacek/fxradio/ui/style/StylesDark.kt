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

import javafx.scene.paint.Color
import javafx.scene.text.FontSmoothingType
import javafx.scene.text.FontWeight
import tornadofx.FXVisibility
import tornadofx.InternalWindow
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.c
import tornadofx.cssclass
import tornadofx.em
import tornadofx.px

/**
 * Dark mode CSS classes used around the app
 */
class StylesDark : Stylesheet() {

    private val colors = DarkAppearance()

    companion object {

        val grayLabel by cssclass()

        val playerMainBox by cssclass()
        val playerStationBox by cssclass()

        val playerControls by cssclass()

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

        val mainMenuBox by cssclass()

        //for Text()
        val primaryTextColor by cssclass()
        val defaultTextColor by cssclass()
        val noBorder by cssclass()

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

        checkBox {
            baseColor = c(colors.primary)
            and(selected) {
                mark {
                    backgroundColor += Color.WHITE
                }
            }
            textFill = c(colors.label)
        }

        playerMainBox {
            padding = box(10.0.px, 0.0.px)
            borderColor += box(
                    c(colors.transparent), c(colors.transparent), c(colors.backgroundBorder), c(colors.transparent)
            )
        }

        playerStationBox {
            padding = box(3.0.px, 10.0.px)
            backgroundRadius += box(3.px)
            borderRadius += box(3.px)
            backgroundColor += c(colors.playerBox)
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
            fontSize = 15.px
        }

        defaultTextColor {
            fill = Color.WHITESMOKE
        }

        primaryTextColor {
            fill = c(colors.primary)
            textFill = c(colors.primary)
        }

        playerControls {
            unsafe("-fx-padding", raw("0"))
            unsafe("-fx-background-insets", raw("0"))
            unsafe("-fx-background-color", raw(colors.background))
            unsafe("-fx-border-color", raw(colors.transparent))
            unsafe("-fx-faint-focus-color", raw(colors.transparent))
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
            baseColor = c(colors.backgroundSelected)
            minHeight = 20.px
        }

        libraryListView {
            backgroundColor += c(colors.background)
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        libraryListItem {
            fontSize = 12.px
            prefHeight = 30.px
            textFill = c(colors.label)
            borderInsets += box(0.px, 5.px, 0.px, 5.px)
            backgroundInsets += box(0.px, 5.px, 0.px, 5.px)
            backgroundColor += c(colors.background)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            and(selected) {
                backgroundColor += c(colors.backgroundSelected)
                borderColor += box(c(colors.backgroundBorder))
                textFill = Color.WHITESMOKE
            }
            padding = box(6.px, 10.px, 6.px, 15.px)
        }

        listItemTag {
            padding = box(2.px)
            textFill = Color.WHITESMOKE
            borderColor += box(c(colors.backgroundBorder))
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            backgroundColor += c(colors.playerBox)
        }

        decoratedListView {
            padding = box(0.px, 10.px, 0.px, 10.px)
            backgroundColor += c(colors.transparent)
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        decoratedListItem {
            fontSize = 12.px
            textFill = c(colors.label)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            and(odd) {
                backgroundColor += c(colors.transparent)
            }
            and(even) {
                backgroundColor += c(colors.background)
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
            backgroundColor += c(colors.background)
        }

        backgroundWhite {
            backgroundColor += c("#262625")
        }

        mainMenuBox {
            borderColor += box(
                    c(colors.transparent), c(colors.transparent), c(colors.backgroundBorder), c(colors.transparent)
            )
        }

        // ===================================================================
        // Restyled default compontents
        // ===================================================================

        tableView {
            baseColor = c(colors.backgroundSelected)
            tableRowCell {
                and(selected) {
                    backgroundColor += c(colors.backgroundSelected)
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
            backgroundColor += c(colors.transparent)
            borderColor += box(c(colors.transparent))

            padding = box(0.px, 5.px, 5.px, 5.px)
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
            textFill = Color.WHITESMOKE
            accentColor = c(colors.primary, 0.2)
            backgroundColor += c(colors.background)
            borderColor += box(c(colors.background))
            content {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.background))
            }
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
            backgroundColor += c(colors.background)

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

        textField {
            backgroundColor += c(colors.playerBox)
            textFill = Color.WHITESMOKE
            promptTextFill = Color.GRAY
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            accentColor = c(colors.primary, 0.2)
            and(focused) {
                borderColor += box(c(colors.primary, 0.6))
                faintFocusColor = c("${colors.primary}22")
            }
        }

        noBorder {
            backgroundInsets += box(0.px)
            padding = box(0.px)
        }

        alert {
            baseColor = c(colors.background)
        }

        separator {
            baseColor = c(colors.transparent)
            line {
                maxWidth = 1.px
            }
        }

        InternalWindow.Styles.floatingWindowWrapper {

            InternalWindow.Styles.top {
                backgroundRadius += box(6.px, 6.px, 0.px, 0.px)
                borderRadius += box(6.px, 6.px, 0.px, 0.px)
                backgroundColor += c(colors.background)
            }

            InternalWindow.Styles.closebutton {
                visibility = FXVisibility.HIDDEN
            }

            InternalWindow.Styles.floatingWindowContent {
                backgroundColor += c(colors.background)
                backgroundRadius += box(0.px, 0.px, 6.px, 6.px)
                borderRadius += box(0.px, 0.px, 6.px, 6.px)
            }
        }
    }
}