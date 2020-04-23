package online.hudacek.broadcastsfx.ui

import javafx.animation.PauseTransition
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.util.Duration
import mu.KotlinLogging
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

private val logger = KotlinLogging.logger {}

operator fun NotificationPane.set(glyph: FontAwesome.Glyph, message: String) {
    if (isVisible) show(message, Glyph("FontAwesome", glyph))
    val delay = PauseTransition(Duration.seconds(5.0))
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

fun createImage(imageview: ImageView, station: Station) {
    if (ImageCache.isImageInCache(station)) {
        logger.debug { "file is in cache, loading" }
        imageview.image = ImageCache.getImageFromCache(station)
    } else {
        logger.debug { "trying to download image from ${station.favicon}" }

        if (station.favicon.isNullOrEmpty() || station.favicon!!.contains(".ico")) {
            logger.debug { "url is empty or unsupported, using default image" }
            imageview.image = Image("Industry-Radio-Tower-icon.png")
            return
        }

        runAsync {
            val conn: URLConnection = URL(station.favicon).openConnection()
            conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)")
            conn.getInputStream().use { stream ->
                logger.debug { "Save to cache" }
                ImageCache.saveImage(station, stream)
            }
        } success {
            imageview.image = ImageCache.getImageFromCache(station)
        } fail {
            logger.debug { "image download failed for $station " }
            it.printStackTrace()
            imageview.image = Image("Industry-Radio-Tower-icon.png")
        }
    }
}