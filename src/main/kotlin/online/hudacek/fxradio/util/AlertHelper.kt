package online.hudacek.fxradio.util

import io.reactivex.rxjava3.core.Maybe
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.stage.Window
import online.hudacek.fxradio.ui.style.Styles
import tornadofx.FX
import tornadofx.addClass
import tornadofx.get
import tornadofx.warning

object AlertHelper {

    /**
     * Shows warning when VLC player is missing
     */
    fun vlcMissingWarning() = warning(FX.messages["player.vlc.missing"], FX.messages["player.vlc.missing.description"])

    /**
     * Shows confirmation alert dialog
     */
    fun confirmAlert(
        header: String, content: String = "", owner: Window? = FX.primaryStage, title: String? = null
    ): Maybe<ButtonType> {
        val alert = Alert(Alert.AlertType.CONFIRMATION, content)
        title?.let { alert.title = it }
        alert.headerText = header
        owner?.also { alert.initOwner(it) }
        val okButton: Button? =
            alert.dialogPane.lookupButton(alert.buttonTypes.firstOrNull { it == ButtonType.OK }) as Button?
        okButton?.addClass(Styles.primaryButton)
        return alert.toMaybe()
            .defaultIfEmpty(ButtonType.CANCEL)
            .filter { it == ButtonType.OK }
    }
}