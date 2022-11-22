/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.ui.util

import javafx.application.Platform.runLater
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.input.Clipboard
import javafx.scene.layout.VBox
import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.style.Styles
import tornadofx.FX
import tornadofx.FX.Companion.primaryStage
import tornadofx.add
import tornadofx.addClass
import tornadofx.setContent
import java.net.URLEncoder

private const val ISSUE_URL = Config.API.repositoryURL +
        "issues/new?assignees=&labels=bug&template=bug_report.md&title="

private val log = KotlinLogging.logger("ErrorHandler")

class CustomErrorHandler : Thread.UncaughtExceptionHandler {

    class ErrorEvent(val thread: Thread, val error: Throwable) {
        internal var consumed = false
        fun consume() {
            consumed = true
        }
    }

    companion object {
        // By default, all error messages are shown. Override to decide if certain errors should be handled another way.
        // Call consume to avoid error dialog.
        var filter: (ErrorEvent) -> Unit = { }
    }

    override fun uncaughtException(t: Thread, error: Throwable) {
        log.error(error) { "Uncaught error" }

        if (error.stackTrace.isNotEmpty()) {
            error.stackTrace[0]?.let {
                if ("okhttp3.internal.connection.RouteSelector.next(RouteSelector.java:75)" in it.toString()) return
            }
        }

        if (isCycle(error)) {
            log.info(error) { "Detected cycle handling error, aborting." }
        } else {
            val event = ErrorEvent(t, error)
            filter(event)

            if (!event.consumed) {
                event.consume()
                runLater {
                    showErrorDialog(error)
                }
            }
        }
    }

    private fun isCycle(error: Throwable) = error.stackTrace.any {
        it.className.startsWith("${javaClass.name}\$uncaughtException$")
    }

    private fun showErrorDialog(error: Throwable) {
        val cause = Label(if (error.cause != null) error.cause?.message else "").apply {
            style = "-fx-font-weight: bold"
        }

        val textarea = TextArea().apply {
            prefRowCount = 20
            prefColumnCount = 50
            text = error.stackTraceToString()
            addClass(Styles.backgroundWhiteSmoke)
        }

        Alert(ERROR).apply {
            initOwner(primaryStage)
            title = error.message ?: "An error occured"
            isResizable = true
            headerText = if (error.stackTrace.isNullOrEmpty()) "Error" else "Error in " + error.stackTrace[0].toString()
            dialogPane.content = VBox().apply {
                add(cause)
                add(textarea)
                addClass(Styles.backgroundWhiteSmoke)
            }

            val reportButton = ButtonType("Report", ButtonBar.ButtonData.HELP)
            val copyButton = ButtonType("Copy", ButtonBar.ButtonData.HELP_2)
            buttonTypes.addAll(reportButton, copyButton)

            val result = showAndWait()
            //Report issue to GitHub
            if (result.get().buttonData == ButtonBar.ButtonData.HELP) {
                val titleQuery = URLEncoder.encode("[${FxRadio.version}] $error", "UTF-8")
                val bodyQuery = URLEncoder.encode(textarea.text, "UTF-8")
                FX.application.hostServices.showDocument("$ISSUE_URL$titleQuery&body=$bodyQuery")
            }

            if (result.get().buttonData == ButtonBar.ButtonData.HELP_2) {
                val clipboard = Clipboard.getSystemClipboard()
                clipboard.setContent {
                    putString(textarea.text)
                }
            }
        }
    }
}
