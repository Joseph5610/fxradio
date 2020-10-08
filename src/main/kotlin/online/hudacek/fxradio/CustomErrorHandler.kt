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

package online.hudacek.fxradio

import javafx.application.Platform.runLater
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import tornadofx.*
import tornadofx.FX.Companion.primaryStage
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.util.logging.Level
import java.util.logging.Logger

class CustomErrorHandler : Thread.UncaughtExceptionHandler {
    private val log = Logger.getLogger("ErrorHandler")

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
        log.log(Level.SEVERE, "Uncaught error", error)

        if (isCycle(error)) {
            log.log(Level.INFO, "Detected cycle handling error, aborting.", error)
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
        }

        Alert(ERROR).apply {
            initOwner(primaryStage)
            title = error.message ?: "An error occured"
            isResizable = true
            headerText = if (error.stackTrace.isNullOrEmpty()) "Error" else "Error in " + error.stackTrace[0].toString()
            dialogPane.content = VBox().apply {
                add(cause)
                add(textarea)
            }

            showAndWait()
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