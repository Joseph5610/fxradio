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

import org.controlsfx.control.NotificationPane
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.FXEvent

class NotificationPaneEvent(val text: String,
                            val glyph: FontAwesome.Glyph = FontAwesome.Glyph.WARNING,
                            val op: NotificationPane.() -> Unit = {}) : FXEvent()