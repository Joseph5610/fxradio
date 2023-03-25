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
 * Dark mode CSS classes used around the app
 */
class StylesDark : Stylesheet() {

    private val colors = DarkAppearance()

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

        val mainMenuBox by cssclass()

        //for Text()
        val primaryTextColor by cssclass()
        val defaultTextColor by cssclass()
        val noBorder by cssclass()

        val glyphIconPrimary by cssclass()
        val glyphIcon by cssclass()

        val notificationPane by cssclass()

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

        checkBox {
            box {
                backgroundColor += c(colors.backgroundSelected)
                backgroundRadius += box(3.px)
                borderRadius += box(3.px)
                accentColor = c(colors.primary, 0.2)
                backgroundInsets += box(0.px)
                borderInsets += box(0.px)
                and(focused) {
                    borderColor += box(c(colors.primary, 0.6))
                    faintFocusColor = c("${colors.primary}22")
                }
            }

            and(selected) {
                box {
                    backgroundColor += c(colors.primary)
                }
                mark {
                    backgroundColor += Color.WHITE
                }
            }
            textFill = c(colors.label)
        }

        playerMainBox {
            padding = box(5.0.px, 35.0.px)
            borderColor += box(
                c(colors.transparent), c(colors.transparent), c(colors.backgroundBorder), c(colors.transparent)
            )
        }

        playerStationBox {
            padding = box(3.0.px)
            backgroundRadius += box(4.px)
            borderRadius += box(4.px)
            backgroundColor += c(colors.playerBox)
            borderColor += box(c(colors.backgroundBorder))
            maxWidth = 300.px
            minWidth = 300.px
            prefWidth = 300.px
            alignment = Pos.CENTER
            prefHeight = 40.px
            minHeight = 40.px
            maxHeight = 40.px
        }

        tag {
            textFill = Color.BLACK
            padding = box(3.0.px, 10.0.px)
            backgroundColor += c(colors.background)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            borderColor += box(c(colors.backgroundBorder))
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

        playerControls {}

        playerControlsBorder {
            and(hover) {
                backgroundRadius += box(6.px)
                backgroundColor += c(colors.grayLabel + "22")
            }
        }

        grayLabel {
            fontSize = 11.px
            textFill = c(colors.grayLabel)
        }

        button {
            backgroundColor += c(colors.backgroundSelected)
            borderColor += box(c(colors.backgroundSelected + "22"))
            minWidth = 75.px
            maxHeight = 25.px
            minHeight = 25.px
            prefHeight = 25.px
            fontSize = 11.px
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            padding = box(0.px)
            textFill = c(colors.label)
            backgroundInsets += box(0.px)
            borderInsets += box(0.px)
            and(pressed) {
                opacity = 0.8
            }
            accentColor = c(colors.primary, 0.2)
            and(focused) {
                borderColor += box(c(colors.primary, 0.6))
                faintFocusColor = c("${colors.primary}22")
            }
        }

        primaryButton {
            backgroundColor += c(colors.primary)
            borderColor += box(c(colors.primary + "22"))
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
            backgroundColor += c(colors.background)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            and(selected) {
                backgroundColor += c(colors.primary)
                borderColor += box(c(colors.primary + "22"))

                label {
                    textFill = Color.WHITESMOKE
                }
            }

            padding = box(6.px, 10.px, 6.px, 10.px)
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
            backgroundColor += c(colors.transparent)
            borderColor += box(c(colors.transparent))
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
        // Restyled default components
        // ===================================================================

        tableView {
            baseColor = c(colors.backgroundSelected)
            tableRowCell {
                and(selected) {
                    unsafe("-fx-table-cell-border-color", raw(colors.backgroundSelected))
                    backgroundColor += c(colors.backgroundSelected)
                    backgroundInsets += box(0.px)
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

            thumb {
                unsafe("-fx-color", raw("whitesmoke"))
            }

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

        glyphIconPrimary {
            textFill = c(colors.primary)
        }

        glyphIcon {
            textFill = c(colors.label)
        }

        colorRadioButton {
            baseColor = c(colors.primary)
            textFill = c(colors.label)
            and(selected) {
                radio {
                    dot {
                        backgroundColor += Color.WHITESMOKE
                        backgroundInsets += box(0.px)
                        backgroundRadius += box(0.3.em)
                    }
                }
            }
        }

        notificationPane {
            baseColor = c(colors.background)
        }

        comboBox {
            backgroundColor += c(colors.backgroundBorder)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            backgroundInsets += box(0.px)
            borderInsets += box(0.px)
            and(pressed) {
                opacity = 0.8
            }
            accentColor = c(colors.primary, 0.2)
            and(focused) {
                borderColor += box(c(colors.primary, 0.6))
                faintFocusColor = c("${colors.primary}22")
            }

            arrow {
                backgroundColor += c(colors.primary)
            }

            listCell {
                textFill = c(colors.label)
            }

            comboBoxPopup {
                listView {
                    backgroundRadius += box(6.px)
                    borderRadius += box(6.px)
                    backgroundColor += c(colors.background)
                }

                listCell {
                    textFill = Color.WHITESMOKE
                    backgroundRadius += box(6.px)
                    borderRadius += box(6.px)
                    backgroundColor += c(colors.background)
                    and(selected) {
                        backgroundColor += c(colors.primary)
                        borderColor += box(c(colors.primary))
                    }
                    padding = box(5.px)
                }
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