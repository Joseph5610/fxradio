package online.hudacek.broadcastsfx.extension

import javafx.animation.Interpolator
import javafx.animation.PauseTransition
import javafx.animation.TranslateTransition
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.text.Text
import javafx.util.Duration
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*


operator fun NotificationPane.set(glyph: FontAwesome.Glyph, message: String) {
    if (isVisible) show(message, Glyph("FontAwesome", glyph))
    val delay = PauseTransition(Duration.seconds(3.0))
    delay.onFinished = EventHandler { hide() }
    delay.play()
}

fun EventTarget.vboxH(height: Double = 20.0): VBox {
    return vbox {
        prefHeight = height
    }
}

fun VBox.tooltip(station: Station): VBox {
    return onHover {
        tooltip(station.name)
    }
}

fun EventTarget.smallLabel(text: String = ""): Label {
    return label(text) {
        addClass(Styles.grayLabel)
    }
}

fun EventTarget.smallIcon(url: String = ""): ImageView {
    return imageview(url) {
        fitWidth = 16.0
        fitHeight = 16.0
    }
}

class Marquee(text: String) : Text(text) {

    private var pnlMain: AnchorPane? = null
    private var pnlHBMain: HBox? = null
    private var marqueeTT: TranslateTransition? = null
    private var speedSec = 18.0
    private var hasPausedME = false
    private var hasPlayedME = false

    init {
        this.style = "-fx-font: bold 20 arial;"
        this.isVisible = true
        this.translateY = this.maxHeight(0.0)
    }

    fun setScrollDuration(seconds: Int) {
        speedSec = seconds.toDouble()
    }

    fun play() {
        marqueeTT!!.play()
    }

    fun pause() {
        marqueeTT!!.pause()
    }

    fun setBoundsFrom(pnl: AnchorPane?) {
        pnlMain = pnl
    }

    fun setBoundsFrom(pnl: HBox) {
        pnlHBMain = pnl
    }

    fun moveDownBy(amount: Int) {
        this.translateY = this.maxHeight(0.0) + amount
    }

    fun setColor(color: String?) {
        this.fill = Paint.valueOf(color)
    }

    fun run() {
        println("run anim")
        marqueeTT = TranslateTransition(Duration.seconds(speedSec), this)
        marqueeTT!!.setOnFinished {
            reRunMarquee()
        }

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(700)
                } catch (ex: InterruptedException) {
                }
                runMarquee()
            }
        }
        thread.start()
    }

    private fun hasPausedME(): Boolean {
        return hasPausedME
    }

    private fun hasPlayedME(): Boolean {
        return hasPlayedME
    }

    private fun setHasPausedME(state: Boolean) {
        hasPausedME = state
    }

    private fun setHasPlayedME(state: Boolean) {
        hasPlayedME = state
    }


    private fun runMarquee() {
        this.setOnMouseEntered {
            if (hasPausedME()) {
                marqueeTT!!.pause()
            } else {
                setHasPausedME(true)
                val thread: Thread = object : Thread() {
                    override fun run() {
                        try {
                            sleep(180)
                        } catch (ex: InterruptedException) {
                        }
                        if (!hasPlayedME()) marqueeTT!!.pause()
                    }
                }
                thread.start()
            }
        }

        this.setOnMouseExited {
            marqueeTT!!.play()
            setHasPlayedME(true)
        }
        reRunMarquee()
    }

    private fun reRunMarquee() {
        println("rerun")
        marqueeTT!!.duration = Duration.seconds(speedSec)
        marqueeTT!!.interpolator = Interpolator.LINEAR
        marqueeTT!!.stop()
        marqueeTT!!.toX = -(this.maxWidth(0.0) + 50)
        if (pnlMain != null) {
            marqueeTT!!.fromX = pnlMain!!.width
        } else {
            marqueeTT!!.fromX = pnlHBMain!!.width
        }
        marqueeTT!!.playFromStart()
        this.isVisible = true
    }
}