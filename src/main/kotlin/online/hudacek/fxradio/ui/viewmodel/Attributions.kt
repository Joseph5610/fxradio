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

package online.hudacek.fxradio.ui.viewmodel

import javafx.beans.property.StringProperty
import tornadofx.ItemViewModel
import tornadofx.observableListOf
import tornadofx.property

object Attributions {
    val list by lazy {
        observableListOf(
                AttributionModel("tornadofx", "1.7.20", Licenses.apache20),
                AttributionModel("controlsfx", "8.40.17", Licenses.controlsfx),
                AttributionModel("vlcj", "4.0", Licenses.gpl3),
                AttributionModel("humble video", "0.3.0", Licenses.gpl3),
                AttributionModel("Retrofit HTTP client", "2.8.1", Licenses.retrofit),
                AttributionModel("slf4j-api", "1.7.5", Licenses.sl4fj),
                AttributionModel("log4j", "2.9.1", Licenses.apache20),
                AttributionModel("kotlin-logging", "1.7.9", Licenses.apache20),
                AttributionModel("FlagIcon", "1.1.0", Licenses.apache20),
                AttributionModel("nsmenufx", "2.1.7", license = Licenses.nsMenuFx),
                AttributionModel("commons-io", "2.6", license = Licenses.apache20),
                AttributionModel("rxkotlinfx", "2.2.2", license = Licenses.apache20),
                AttributionModel("sqliteJdbc", "3.21.0.1", license = Licenses.apache20),
                AttributionModel("rxkotlin-jdbc", "0.4.1", license = Licenses.apache20),
                AttributionModel("TickerView", license = Licenses.tickerView),
                AttributionModel("macOS install disk background", license = Licenses.bg),
                AttributionModel("Application logo, radio station icon", license = Licenses.iconArchive)
        )
    }
}

data class License(val name: String = "", val content: String)

class AttributionModel(name: String, version: String = "", license: License) {
    var name: String by property(name)
    var version: String by property(version)
    var licenseContent: String by property(license.content)
    var licenseName: String by property(license.name)
}

/**
 * Attribution view model
 * -------------------
 * Handles information about Licensing
 * Used in [online.hudacek.fxradio.ui.fragment.AttributionsFragment]
 */
class AttributionViewModel : ItemViewModel<AttributionModel>() {
    val nameProperty = bind(AttributionModel::name) as StringProperty
    val versionProperty = bind(AttributionModel::version) as StringProperty
    val licenseNameProperty = bind(AttributionModel::licenseName) as StringProperty
    val licenseContentProperty = bind(AttributionModel::licenseContent) as StringProperty
}

private object Licenses {
    val bg = License(content = "Designed by xb100 / Freepik")
    val iconArchive = License(content = "Obtained from http://iconarchive.com")
    val retrofit = License("Apache License 2.0", "Copyright 2013 Square, Inc.\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "   http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.")
    val gpl3 = License("GPL v3", "This program is free software: you can redistribute it and/or modify\n" +
            "    it under the terms of the GNU General Public License as published by\n" +
            "    the Free Software Foundation, either version 3 of the License, or\n" +
            "    (at your option) any later version.\n" +
            "\n" +
            "    This program is distributed in the hope that it will be useful,\n" +
            "    but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
            "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
            "    GNU General Public License for more details.\n" +
            "\n" +
            "    You should have received a copy of the GNU General Public License\n" +
            "    along with this program.  If not, see <https://www.gnu.org/licenses/>.")
    val sl4fj = License("MIT License", "Copyright (c) 2004-2017 QOS.ch\n" +
            " All rights reserved.\n" +
            "\n" +
            " Permission is hereby granted, free  of charge, to any person obtaining\n" +
            " a  copy  of this  software  and  associated  documentation files  (the\n" +
            " \"Software\"), to  deal in  the Software without  restriction, including\n" +
            " without limitation  the rights to  use, copy, modify,  merge, publish,\n" +
            " distribute,  sublicense, and/or sell  copies of  the Software,  and to\n" +
            " permit persons to whom the Software  is furnished to do so, subject to\n" +
            " the following conditions:\n" +
            " \n" +
            " The  above  copyright  notice  and  this permission  notice  shall  be\n" +
            " included in all copies or substantial portions of the Software.\n" +
            " \n" +
            " THE  SOFTWARE IS  PROVIDED  \"AS  IS\", WITHOUT  WARRANTY  OF ANY  KIND,\n" +
            " EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF\n" +
            " MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND\n" +
            " NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE\n" +
            " LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION\n" +
            " OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION\n" +
            " WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.")
    val apache20 = License("Apache License, version 2.0", "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "    http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.")

    val tickerView = License(content = "https://gitlab.light.kow.is/kowis-projects/deskscreen")
    val nsMenuFx = License("BSD 3-Clause \"New\" or \"Revised\" License", "Copyright (c) 2015, codecentric AG\n" +
            "All rights reserved.\n" +
            "\n" +
            "Redistribution and use in source and binary forms, with or without\n" +
            "modification, are permitted provided that the following conditions are met:\n" +
            "    * Redistributions of source code must retain the above copyright\n" +
            "      notice, this list of conditions and the following disclaimer.\n" +
            "    * Redistributions in binary form must reproduce the above copyright\n" +
            "      notice, this list of conditions and the following disclaimer in the\n" +
            "      documentation and/or other materials provided with the distribution.\n" +
            "    * Neither the name of the codecentric AG nor the\n" +
            "      names of its contributors may be used to endorse or promote products\n" +
            "      derived from this software without specific prior written permission.\n" +
            "\n" +
            "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND\n" +
            "ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n" +
            "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n" +
            "DISCLAIMED. IN NO EVENT SHALL CODECENTRIC AG BE LIABLE FOR ANY\n" +
            "DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\n" +
            "(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\n" +
            "LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\n" +
            "ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n" +
            "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\n" +
            "SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.")
    val semver = License("The MIT License (MIT)", "The MIT License (MIT)\n" +
            "\n" +
            "Copyright (c) 2015-present Vincent DURMONT vdurmont@gmail.com\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.")
    val controlsfx = License("BSD 3-Clause license", "BSD 3-Clause License\n" +
            "\n" +
            "Copyright (c) 2013, ControlsFX\n" +
            "All rights reserved.\n" +
            "\n" +
            "Redistribution and use in source and binary forms, with or without\n" +
            "modification, are permitted provided that the following conditions are met:\n" +
            "\n" +
            "1. Redistributions of source code must retain the above copyright notice, this\n" +
            "   list of conditions and the following disclaimer.\n" +
            "\n" +
            "2. Redistributions in binary form must reproduce the above copyright notice,\n" +
            "   this list of conditions and the following disclaimer in the documentation\n" +
            "   and/or other materials provided with the distribution.\n" +
            "\n" +
            "3. Neither the name of ControlsFX, any associated website, nor the names of its\n" +
            "   contributors may be used to endorse or promote products derived from\n" +
            "   this software without specific prior written permission.\n" +
            "\n" +
            "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"\n" +
            "AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE\n" +
            "IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n" +
            "DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE\n" +
            "FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL\n" +
            "DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR\n" +
            "SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER\n" +
            "CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,\n" +
            "OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n" +
            "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.")
}