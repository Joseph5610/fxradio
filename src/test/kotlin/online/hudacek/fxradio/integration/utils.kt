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

package online.hudacek.fxradio.integration

import javafx.scene.Node
import javafx.scene.control.Labeled
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyCode
import javafx.scene.text.Text
import mu.KotlinLogging
import org.hamcrest.Matcher
import org.junit.jupiter.api.Assertions.fail
import org.testfx.api.FxRobot
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.control.LabeledMatchers
import org.testfx.matcher.control.TextInputControlMatchers
import org.testfx.matcher.control.TextMatchers
import org.testfx.util.WaitForAsyncUtils
import java.util.concurrent.TimeUnit

//Test utils methods
private val logger = KotlinLogging.logger("TestUtils")

/**
 * Wait some time for action to finish
 */
internal fun waitFor(seconds: Long, op: () -> Boolean = { false }) {
    runCatching {
        logger.info { "Wait $seconds secs for operation to finish" }
        WaitForAsyncUtils.waitFor(seconds, TimeUnit.SECONDS) {
            op()
        }
    }.onFailure { fail<Unit>("waitFor $op didn't finish in $seconds secs.") }
}

internal fun sleep(seconds: Long) {
    logger.info { "Sleep for $seconds secs" }
    WaitForAsyncUtils.sleep(seconds, TimeUnit.SECONDS)
}

internal fun hasLabel(txt: String): Matcher<Labeled> {
    logger.info { "Check element has text: $txt " }
    return LabeledMatchers.hasText(txt)
}

internal fun hasText(txt: String): Matcher<Text> {
    logger.info { "Check element has text: $txt" }
    return TextMatchers.hasText(txt)
}

internal fun hasValue(txt: String): Matcher<TextInputControl> {
    logger.info { "Check TextField has value: $txt" }
    return TextInputControlMatchers.hasText(txt)
}

internal fun visible() = NodeMatchers.isVisible()

internal inline fun <reified T : Node> FxRobot.find(name: String): T {
    logger.info { "Find element: $name" }
    return lookup(name).query() as T
}

internal inline fun <reified T : Node> FxRobot.findAll(name: String): Set<T> {
    logger.info { "Find element: $name" }
    return lookup(name).queryAll()
}

internal fun FxRobot.enterText(fieldQuery: String, textToEnter: String) {
    logger.info { "Entering text: $textToEnter" }
    // Remove existing value
    doubleClickOn(find(fieldQuery) as TextField)
    press(KeyCode.DELETE)
    write(textToEnter)
}
