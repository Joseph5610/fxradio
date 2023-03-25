package online.hudacek.fxradio.ui.util

import javafx.scene.image.Image

/**
 * Helper class to load flag icons
 */
class FlagIcon(isoCountryCode: String) {

    private val flagPath = "/flags/${isoCountryCode.lowercase()}.png"

    fun get(): Image? = runCatching {
        Image(javaClass.getResourceAsStream(flagPath))
    }.getOrNull()
}