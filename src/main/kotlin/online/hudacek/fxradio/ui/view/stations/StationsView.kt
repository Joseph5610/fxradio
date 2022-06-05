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

package online.hudacek.fxradio.ui.view.stations

import javafx.scene.layout.Priority
import online.hudacek.fxradio.ui.BaseView
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.view.player.TickerView
import tornadofx.addClass
import tornadofx.fitToParentHeight
import tornadofx.vbox
import tornadofx.vgrow

/**
 * Main left pane of the app
 */
class StationsView : BaseView() {

    private val messageView: StationsEmptyView by inject()
    private val headerView: StationsHeaderView by inject()
    private val dataGridView: StationsDataGridView by inject()
    private val historyView: StationsHistoryView by inject()

    override val root = vbox {
        vgrow = Priority.ALWAYS
        add(headerView)
        add(messageView)
        add(dataGridView)
        add(historyView)
        dataGridView.root.fitToParentHeight()
        historyView.root.fitToParentHeight()
        addClass(Styles.backgroundWhite)
    }
}