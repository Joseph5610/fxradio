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

package online.hudacek.fxradio.ui

import javafx.application.Platform.runLater
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.input.Clipboard
import javafx.scene.layout.VBox
import mu.KotlinLogging
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.style.Styles
import tornadofx.FX
import tornadofx.FX.Companion.primaryStage
import tornadofx.add
import tornadofx.addClass
import tornadofx.setContent
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.net.URLEncoder

private const val issueUrl = "https://github.com/Joseph5610/fxradio-main/issues/new?assignees=&labels=bug&template=bug_report.md&title="

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

        if (error.stackTrace[0].toString()
                        .contains("okhttp3.internal.connection.RouteSelector.next(RouteSelector.java:75)"))
            return

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
            text = stringFromError(error)
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

            val reportButton = ButtonType("Report issue", ButtonBar.ButtonData.HELP)
            val copyButton = ButtonType("Copy to clipboard", ButtonBar.ButtonData.HELP_2)
            buttonTypes.addAll(reportButton, copyButton)

            val result = showAndWait()
            //Report issue to github
            if (result.get().buttonData == ButtonBar.ButtonData.HELP) {
                val titleQuery = URLEncoder.encode("[${FxRadio.version}] $error", "UTF-8")
                val bodyQuery = URLEncoder.encode(textarea.text, "UTF-8")
                FX.application.hostServices.showDocument("$issueUrl$titleQuery&body=$bodyQuery")
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

private fun stringFromError(e: Throwable): String {
    val out = ByteArrayOutputStream()
    val writer = PrintWriter(out)
    e.printStackTrace(writer)
    writer.close()
    return out.toString()
}