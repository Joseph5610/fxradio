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

package online.hudacek.fxradio.util

import online.hudacek.fxradio.viewmodel.Attribution
import online.hudacek.fxradio.viewmodel.License
import tornadofx.observableListOf

object Attributions {

    val list by lazy {
        observableListOf(
            Attribution("tornadofx", "2.0.0-SNAPSHOT", Licenses.apache20),
            Attribution("controlsfx", "11.1.2", Licenses.controlsfx),
            Attribution("vlcj", "4.8.2", Licenses.gpl3),
            Attribution("humble video", "0.3.0", Licenses.agpl3),
            Attribution("Retrofit HTTP client", "2.9.0", Licenses.retrofit),
            Attribution("slf4j-api", "2.0.7", Licenses.sl4fj),
            Attribution("log4j", "2.20.0", Licenses.apache20),
            Attribution("kotlin-logging", "3.0.5", Licenses.apache20),
            Attribution("nsmenufx", "3.1.10", license = Licenses.nsMenuFx),
            Attribution("rxjavafx", "3.0.2", license = Licenses.apache20),
            Attribution("sqliteJdbc", "3.42.0.0", license = Licenses.apache20),
            Attribution("rxjava3-jdbc", "0.1.3", license = Licenses.apache20),
            Attribution("TickerView", license = Licenses.tickerView),
            Attribution("Application Graphics", license = Licenses.graphics)
        )
    }

    private object Licenses {
        val graphics = License(
            content = "macOS install disk background: Designed by xb100 / Freepik\n\n" +
                    "Voice chat icons created by Rizki Ahmad Fauzi - Flaticon\n\n" +
                    "Flag Icons based on https://github.com/griffon-legacy/griffon-countries-javafx-plugin"
        )
        val retrofit = License(
            "Apache License 2.0", "Copyright 2013 Square, Inc.\n" +
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
                    "limitations under the License."
        )
        val gpl3 = License(
            "GPL v3", "This program is free software: you can redistribute it and/or modify\n" +
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
                    "    along with this program.  If not, see <https://www.gnu.org/licenses/>."
        )
        val agpl3 = License(
            "AGPL v3", "Copyright (c) 2014, Andrew \"Art\" Clarke.  All rights reserved.\n" +
                    " *   \n" +
                    " *\n" +
                    " * Humble-Video is free software: you can redistribute it and/or modify\n" +
                    " * it under the terms of the GNU Affero General Public License as published by\n" +
                    " * the Free Software Foundation, either version 3 of the License, or\n" +
                    " * (at your option) any later version.\n" +
                    " *\n" +
                    " * Humble-Video is distributed in the hope that it will be useful,\n" +
                    " * but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                    " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                    " * GNU Affero General Public License for more details.\n" +
                    " *\n" +
                    " * You should have received a copy of the GNU Affero General Public License\n" +
                    " * along with Humble-Video.  If not, see <http://www.gnu.org/licenses/>."
        )
        val sl4fj = License(
            "MIT License", "Copyright (c) 2004-2017 QOS.ch\n" +
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
                    " WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."
        )
        val apache20 = License(
            "Apache License, version 2.0", "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    "you may not use this file except in compliance with the License.\n" +
                    "You may obtain a copy of the License at\n" +
                    "\n" +
                    "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                    "\n" +
                    "Unless required by applicable law or agreed to in writing, software\n" +
                    "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    "See the License for the specific language governing permissions and\n" +
                    "limitations under the License."
        )

        val tickerView = License(content = "https://gitlab.light.kow.is/kowis-projects/deskscreen")
        val nsMenuFx = License(
            "BSD 3-Clause \"New\" or \"Revised\" License", "Copyright (c) 2015, codecentric AG\n" +
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
                    "SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."
        )
        val controlsfx = License(
            "BSD 3-Clause license", "BSD 3-Clause License\n" +
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
                    "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."
        )
    }
}
