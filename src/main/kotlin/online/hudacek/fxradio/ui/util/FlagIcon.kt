package online.hudacek.fxradio.ui.util

import javafx.scene.image.Image
import tornadofx.Component

/**
 * Helper class to load flag icons
 */
class FlagIcon(isoCountryCode: String) : Component() {

    private val flagPath = "/flags/$isoCountryCode.png"

    fun get(): Image? = runCatching {
        Image(app.resources.stream(flagPath))
    }.getOrNull()
}