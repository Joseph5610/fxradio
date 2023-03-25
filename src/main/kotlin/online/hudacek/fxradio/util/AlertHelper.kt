package online.hudacek.fxradio.util

import io.reactivex.rxjava3.core.Maybe
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Window
import tornadofx.FX
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
        return alert.toMaybe()
            .defaultIfEmpty(ButtonType.CANCEL)
            .filter { it == ButtonType.OK }
    }
}