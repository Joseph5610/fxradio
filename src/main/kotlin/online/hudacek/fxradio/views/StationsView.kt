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

package online.hudacek.fxradio.views

import javafx.scene.layout.Priority
import online.hudacek.fxradio.styles.Styles
import tornadofx.*

/**
 * Main view displaying grid of stations
 */
class StationsView : View() {

    private val messageView: StationsMessageView by inject()
    private val headerView: StationsHeaderView by inject()
    private val dataGridView: StationsDataGridView by inject()

    override val root = vbox {
        addClass(Styles.backgroundWhite)
        vgrow = Priority.ALWAYS
        add(messageView)
        add(headerView)
        add(dataGridView)
        dataGridView.root.fitToParentHeight()
        messageView.root.prefWidthProperty().bind(widthProperty())
    }
}