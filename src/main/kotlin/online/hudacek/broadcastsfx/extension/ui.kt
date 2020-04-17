package online.hudacek.broadcastsfx.extension

import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import online.hudacek.broadcastsfx.model.Station
import online.hudacek.broadcastsfx.styles.Styles
import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.*

operator fun NotificationPane.set(glyph: FontAwesome.Glyph, message: String) {
    if (isVisible) show(message, Glyph("FontAwesome", glyph))
}

fun EventTarget.vboxH(height: Double = 20.0): VBox {
    return vbox {
        prefHeight = height
    }
}

fun EventTarget.vboxP(padding: Double = 20.0): VBox {
    return vbox {
        paddingAll = padding
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