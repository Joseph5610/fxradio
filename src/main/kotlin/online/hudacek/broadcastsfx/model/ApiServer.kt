package online.hudacek.broadcastsfx.model

import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

class ApiServer(url: String) {
    var url: String by property(url)
    fun urlProperty() = getProperty(ApiServer::url)
}

class ApiServerViewModel : ItemViewModel<ApiServer>() {
    val url = bind { item?.urlProperty() }
}