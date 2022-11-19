package online.hudacek.fxradio.util

import com.github.thomasnield.rxkotlinfx.toMaybe
import io.reactivex.Maybe
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Window
import tornadofx.FX
import tornadofx.get
import tornadofx.warning

object AlertHelper {

    /**
     * Shows when VLC player is missing
     */
    fun vlcMissingAlert() = warning(FX.messages["player.vlc.missing"], FX.messages["player.vlc.missing.description"])

    /**
     * Shows confirmation alert dialog
     */
    fun confirmAlert(
        header: String, content: String = "",
        confirmButton: ButtonType = ButtonType.OK,
        cancelButton: ButtonType = ButtonType.CANCEL,
        owner: Window? = FX.primaryStage, title: String? = null
    ): Maybe<ButtonType> {
        val alert = Alert(Alert.AlertType.CONFIRMATION, content, confirmButton, cancelButton)
        title?.let { alert.title = it }
        alert.headerText = header
        owner?.also { alert.initOwner(it) }
        return alert.toMaybe()
            .defaultIfEmpty(ButtonType.CANCEL)
            .filter { it == ButtonType.OK }
    }
}