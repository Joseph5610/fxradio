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

package online.hudacek.fxradio.ui.view.menu

import javafx.scene.control.Menu
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.viewmodel.AppMenuViewModel
import tornadofx.Controller

abstract class BaseMenu : Controller() {

    protected val appMenuViewModel: AppMenuViewModel by inject()
    protected val appEvent: AppEvent by inject()

    abstract val menu: Menu
}
