package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import tornadofx.*

class PlayerView : View() {

    override val root = vbox {
        prefHeight = 80.0
        paddingTop = 20.0
        hbox(15) {
            alignment = Pos.CENTER_LEFT
            paddingLeft = 30.0
            imageview("Media-Controls-Play-icon.png") {
                fitWidth = 30.0
                fitHeight = 30.0
                isPreserveRatio = true
            }
            region {
                hgrow = Priority.ALWAYS
            }
            vbox {
                style {
                    padding = box(3.px, 10.px, 3.px, 10.px)
                    backgroundRadius = multi(box(3.px))
                    backgroundColor = multi(Paint.valueOf("#E9E9E9"))
                    borderRadius = multi(box(3.px))
                    borderColor += box(c("#E8E8E8"))
                }
                hbox(5) {
                    imageview("Clouds-icon.png") {
                        fitWidth = 30.0
                        fitHeight = 30.0
                        isPreserveRatio = true
                    }
                    separator(Orientation.VERTICAL)
                    vbox {
                        paddingLeft = 10.0
                        paddingRight = 10.0
                        label("Now playing...")
                        label("Radio Name") {
                            style {
                                fontWeight = FontWeight.EXTRA_BOLD
                            }
                        }
                    }
                }

            }
            region {
                hgrow = Priority.ALWAYS
            }
            hbox {
                paddingRight = 30.0
                alignment = Pos.CENTER_LEFT
                imageview("Media-Controls-Volume-Down-icon.png") {
                    fitWidth = 16.0
                    fitHeight = 16.0
                    isPreserveRatio = true
                }
                slider(0..100)
                imageview("Media-Controls-Volume-Up-icon.png") {
                    fitWidth = 16.0
                    fitHeight = 16.0
                    isPreserveRatio = true
                }
            }
        }

    }
}