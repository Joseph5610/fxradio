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

import javafx.scene.Node
import javafx.scene.control.Labeled
import org.hamcrest.Matcher
import org.junit.jupiter.api.Assertions.fail
import org.testfx.api.FxRobot
import org.testfx.matcher.control.LabeledMatchers
import org.testfx.util.WaitForAsyncUtils
import java.util.concurrent.TimeUnit

//Test utils methods

/**
 * Wait some time for action to finish
 */
internal fun waitFor(seconds: Long, op: () -> Boolean = { false }) =
        try {
            println("Wait $seconds secs for operation to finish")
            WaitForAsyncUtils.waitFor(seconds, TimeUnit.SECONDS) {
                op.invoke()
            }
        } catch (e: Throwable) {
            fail<Unit>("waitFor $op didn't finish in $seconds secs.", e)
        }

internal fun sleep(seconds: Long) {
    println("Sleep for $seconds secs")
    WaitForAsyncUtils.sleep(seconds, TimeUnit.SECONDS)
}

internal fun hasText(txt: String): Matcher<Labeled> {
    println("Check element has text: $txt ")
    return LabeledMatchers.hasText(txt)
}

internal fun <T : Node> FxRobot.find(name: String): T {
    println("Find element: $name ")
    return lookup(name).query()
}