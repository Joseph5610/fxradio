package online.hudacek.broadcastsfx.model

import tornadofx.ItemViewModel
import tornadofx.property

class ApiServer(url: String) {
    var url: String by property(url)
}

class ApiServerModel : ItemViewModel<ApiServer>() {
    val url = bind(ApiServer::url)
}