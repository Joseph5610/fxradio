package online.hudacek.fxradio.util

import io.reactivex.rxjava3.core.Maybe
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.layout.Region
import online.hudacek.fxradio.ui.style.Styles
import tornadofx.FX
import tornadofx.addClass

object RxAlert {

    /**
     * Shows confirmation alert dialog
     */
    fun confirm(header: String, content: String) = rxAlert(type = AlertType.CONFIRMATION, header, content)

    /**
     * Shows warning alert dialog
     */
    fun warning(header: String, content: String) = rxAlert(type = AlertType.WARNING, header, content)

    /**
     * Constructs reactive alert modal dialog
     */
    private fun rxAlert(
        type: AlertType = AlertType.INFORMATION,
        header: String,
        content: String,
        title: String? = null
    ): Maybe<ButtonType> {
        val alert = Alert(type, content)
        title?.let { alert.title = it }
        alert.headerText = header
        alert.initOwner(FX.primaryStage)
        alert.dialogPane.minHeight = Region.USE_PREF_SIZE

        val okButton: Button? =
            alert.dialogPane.lookupButton(alert.buttonTypes.firstOrNull { it == ButtonType.OK }) as Button?
        okButton?.addClass(Styles.primaryButton)
        return alert.toMaybe()
            .defaultIfEmpty(ButtonType.CANCEL)
            .filter { it == ButtonType.OK }
    }
}