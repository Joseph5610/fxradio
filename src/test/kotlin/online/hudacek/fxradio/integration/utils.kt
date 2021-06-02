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
internal fun invisible() = NodeMatchers.isInvisible()

internal inline fun <reified T : Node> FxRobot.find(name: String): T {
    logger.info { "Find element: $name" }
    return lookup(name).query() as T
}

internal inline fun <reified T : Node> FxRobot.findAll(name: String): Set<T> {
    logger.info { "Find element: $name" }
    return lookup(name).queryAll()
}

internal fun FxRobot.enterText(fieldQuery: String, textToEnter: String) {
    //Remove existing value
    doubleClickOn(find(fieldQuery) as TextField)
    press(KeyCode.DELETE)
    write(textToEnter)
}