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
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.util.Duration
import mu.KotlinLogging
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*
import tornadofx.controlsfx.glyph
import java.net.URL

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

internal fun VBox.tooltip(station: Station) =
        onHover {
            tooltip(station.name)
        }

internal fun EventTarget.smallLabel(text: String = "") =
        label(text) {
            paddingLeft = 10.0
            addClass(Styles.boldText)
            addClass(Styles.grayLabel)
        }

internal fun EventTarget.smallIcon(url: String = "", op: ImageView.() -> Unit = {}) =
        imageview(url, op = op).apply {
            fitWidth = 14.0
            fitHeight = 14.0
        }

/**
 * Convenience methods for boolean bindings
 */
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

    if (!station.isValidStation()) return

    if (ImageCache.isImageInCache(station)) {
        this.image = ImageCache.getImageFromCache(station)
    } else {
        if (station.isInvalidImage()) {
            logger.debug { "Image for ${station.name} is invalid." }
            return
        }

        logger.debug { "downloading logo of ${station.name} from ${station.favicon}" }

        runAsync {
            URL(station.favicon).openConnection().apply {
                setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)")
                getInputStream().use { stream ->
                    ImageCache.saveImage(station, stream)
                }
            }
        } success {
            this.image = ImageCache.getImageFromCache(station)
        } fail {
            logger.error {
                "image download failed for ${station.name} (${it.localizedMessage}) "
            }
            this.image = defaultRadioLogo
        }
    }
}

internal fun ImageView.downloadImage(url: String) = runAsync {
    URL(url).openConnection().apply {
        setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)")
    }
} success {
    this.image = it.getInputStream().use { stream ->
        Image(stream)
    }
    //println(this.image.isError)
    this.image.exception.printStackTrace()
} fail {
    this.image = defaultRadioLogo
}

internal fun EventTarget.glyph(glyph: FontAwesome.Glyph) =
        glyph("FontAwesome", glyph) {
            size(35.0)
            style {
                padding = box(10.px, 5.px)
            }
        }

/**
 * Open URL in user's internet browser
 */
internal fun App.openUrl(url: String) {
    val hostServices = HostServicesFactory.getInstance(this)
    hostServices.showDocument(url)
}

internal val defaultRadioLogo = Image(Config.Paths.defaultRadioIcon)