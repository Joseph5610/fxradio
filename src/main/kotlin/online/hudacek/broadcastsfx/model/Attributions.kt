package online.hudacek.broadcastsfx.model

import tornadofx.*

object Attributions {
    val list = observableListOf(
            Attribution("tornadofx", "1.7.20", Licenses.apache20),
            Attribution("controlsfx", "8.40.16", Licenses.controlsfx),
            Attribution("vlcj", "4.0", Licenses.gpl3),
            Attribution("humble video", "0.3.0", Licenses.gpl3),
            Attribution("Retrofit HTTP client", "2.8.1", Licenses.retrofit),
            Attribution("slf4j-api", "1.7.5", Licenses.sl4fj),
            Attribution("log4j", "2.9.1", Licenses.apache20),
            Attribution("kotlin-logging", "1.7.9", Licenses.apache20),
            Attribution("appdmg", "0.6.0", Licenses.appdmg),
            Attribution("macOS install disk background", "", Licenses.bg),
            Attribution("Application logo, radio station icon, play, pause, volume icons", "", Licenses.iconArchive)
    )
}

class License(name: String, content: String) {
    var name: String by property(name)
    var content: String by property(content)
}

class Attribution(name: String, version: String, license: License) {
    var name: String by property(name)
    var version: String by property(version)
    var licenseContent: String by property(license.content)
    var licenseName: String by property(license.name)
}

class AttributionModel : ItemViewModel<Attribution>() {
    val name = bind(Attribution::name)
    val licenseName = bind(Attribution::licenseName)
    val licenseContent = bind(Attribution::licenseContent)
}

private object Licenses {
    val bg = License("", "Designed by xb100 / Freepik")
    val iconArchive = License("", "Obtained from http://iconarchive.com")
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

    val appdmg = License("MIT", "The MIT License (MIT)\n" +
            "\n" +
            "Copyright (c) 2013 Linus Unnebäck\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy of\n" +
            "this software and associated documentation files (the \"Software\"), to deal in\n" +
            "the Software without restriction, including without limitation the rights to\n" +
            "use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of\n" +
            "the Software, and to permit persons to whom the Software is furnished to do so,\n" +
            "subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all\n" +
            "copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS\n" +
            "FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR\n" +
            "COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER\n" +
            "IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN\n" +
            "CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.")

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