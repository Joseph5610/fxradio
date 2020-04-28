package online.hudacek.broadcastsfx.ui

import javafx.animation.PauseTransition
import javafx.beans.property.Property
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.util.Duration
import mu.KotlinLogging
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*
import java.net.URL
import java.net.URLConnection

/*
 * Helper extension functions for UI
 */
private val logger = KotlinLogging.logger {}

/**
 * Custom function for showing notification in NotificationPane.
 * Notification disappears after 5 seconds
 *
 * Example usage:
 * notificationPane[FontAwesome.Glyph.WARNING] = "Custom notification Text"
 */
internal operator fun NotificationPane.set(glyph: FontAwesome.Glyph, message: String) {
    if (isVisible) show(message, Glyph("FontAwesome", glyph))
    val delay = PauseTransition(Duration.seconds(5.0))
    delay.onFinished = EventHandler { hide() }
    delay.play()
}

internal fun VBox.tooltip(station: Station): VBox {
    return onHover {
        tooltip(station.name)
    }
}

internal fun EventTarget.smallLabel(text: String = ""): Label {
    return label(text) {
        addClass(Styles.grayLabel)
    }
}

internal fun EventTarget.smallIcon(url: String = ""): ImageView {
    return imageview(url) {
        fitWidth = 16.0
        fitHeight = 16.0
    }
}

/**
 * Convenience methods for MenuItems
 */
internal fun MenuItem.shouldBeVisible(station: Property<Station>) {
    visibleProperty().bind(booleanBinding(station) {
        value != null
    })
}

internal fun MenuItem.shouldBeDisabled(station: Property<Station>) {
    disableProperty().bind(booleanBinding(station) {
        value == null
    })
}

/**
 * This method is used for custom downloading of station's logo
 * and storing it in cache directory
 *
 * It is using custom URLConnection with fake user-agent because some servers deny
 * response when no user agent is send
 *
 * In case of error Industry-Radio-Tower-icon static png file is used as station logo
 */
internal fun ImageView.createImage(station: Station) {
    if (ImageCache.isImageInCache(station)) {
        logger.debug { "file is in cache, loading" }
        this.image = ImageCache.getImageFromCache(station)
    } else {
        logger.debug { "trying to download image from ${station.favicon}" }

        if (station.isInvalidImage()) {
            logger.debug { "url is empty or unsupported, using default image" }
            this.image = Image("Industry-Radio-Tower-icon.png")
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
            this.image = ImageCache.getImageFromCache(station)
        } fail {
            logger.debug { "image download failed for $station " }
            it.printStackTrace()
            this.image = Image("Industry-Radio-Tower-icon.png")
        }
    }
}