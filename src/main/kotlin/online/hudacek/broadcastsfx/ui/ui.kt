package online.hudacek.broadcastsfx.ui

import javafx.animation.PauseTransition
import javafx.beans.property.Property
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
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
        paddingLeft = 10.0
        style {
            fontWeight = FontWeight.BOLD
        }
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
 * Convenience methods for boolean bindings
 */
internal fun Node.shouldBeVisible(station: Property<Station>) {
    visibleWhen(booleanBinding(station) {
        value != null
    })
}

internal fun MenuItem.shouldBeVisible(station: Property<Station>) {
    visibleWhen(booleanBinding(station) {
        value != null && value.isValidStation()
    })
}

internal fun MenuItem.shouldBeDisabled(station: Property<Station>) {
    disableWhen(booleanBinding(station) {
        value == null || !value.isValidStation()
    })
}

internal fun Node.shouldBeDisabled(station: Property<Station>) {
    disableWhen(booleanBinding(station) {
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
    this.image = Image("Industry-Radio-Tower-icon.png")

    if (ImageCache.isImageInCache(station)) {
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
                ImageCache.saveImage(station, stream)
            }
        } success {
            this.image = ImageCache.getImageFromCache(station)
        } fail {
            logger.error(it) {
                "image download failed for ${station.stationuuid} "
            }
            this.image = Image("Industry-Radio-Tower-icon.png")
        }
    }
}

internal fun osNotification() {
    /*
    runAsync {
       // if(Utils.isMacOs) {
         //   Runtime.getRuntime().exec(arrayOf("osascript", "-e", "display notification \"This is a message\" with title \"Title\" subtitle \"Subtitle\" sound name \"Funk\""))
      //  } else {
            if (SystemTray.isSupported()) {
                val tray = SystemTray.getSystemTray()
                val image: java.awt.Image = Toolkit.getDefaultToolkit().createImage("Industry-Radio-Tower-icon.png")
                val trayIcon = TrayIcon(image, "Tray Demo")
                trayIcon.isImageAutoSize = true
                trayIcon.toolTip = "System tray icon demo"
                tray.add(trayIcon)

                trayIcon.displayMessage("Hello, World", "notification demo", MessageType.INFO)

            } else {
                println("not supported")
            }


       // }
    }
       */
}