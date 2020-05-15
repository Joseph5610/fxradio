/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.broadcastsfx.extension

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory
import javafx.animation.PauseTransition
import javafx.beans.property.Property
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import javafx.util.Duration
import mu.KotlinLogging
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.glyph
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
    if (isVisible) show(message, glyph("FontAwesome", glyph))
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

internal fun CheckMenuItem.shouldBeSelected(playerType: Property<PlayerType>) {
    selectedProperty().booleanBinding(playerType) {
        playerType.value == PlayerType.Native
    }
}

internal fun CheckMenuItem.select(booleanProperty: Property<Boolean>) {
    selectedProperty().booleanBinding(booleanProperty) {
        it == true
    }
}

internal fun MenuItem.shouldBeDisabled(station: Property<Station>) {
    disableWhen(booleanBinding(station) {
        value == null || !value.isValidStation()
    })
}

internal fun Node.shouldBeDisabled(station: Property<Station>) {
    disableWhen(booleanBinding(station) {
        value == null || !value.isValidStation()
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
    this.image = defaultRadioLogo

    if (ImageCache.isImageInCache(station)) {
        this.image = ImageCache.getImageFromCache(station)
    } else {
        logger.debug { "trying to download image from ${station.favicon}" }

        if (station.isInvalidImage()) {
            logger.debug { "url is empty or unsupported, using default image" }
            this.image = defaultRadioLogo
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
            this.image = defaultRadioLogo
        }
    }
}

internal fun EventTarget.glyph(glyph: FontAwesome.Glyph) =
        glyph("FontAwesome", glyph) {
            size(35.0)
            padding = Insets(10.0, 5.0, 10.0, 5.0)
        }

/**
 * Open URL in user's internet browser
 */
internal fun App.openUrl(url: String) {
    val hostServices = HostServicesFactory.getInstance(this)
    hostServices.showDocument(url)
}

internal val defaultRadioLogo = Image(Config.Paths.defaultRadioIcon)