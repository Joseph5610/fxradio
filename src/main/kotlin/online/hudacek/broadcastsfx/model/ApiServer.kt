package online.hudacek.broadcastsfx.model

import online.hudacek.broadcastsfx.StationsApiClient
import tornadofx.ItemViewModel
import tornadofx.property

class ApiServer(url: String) {
    var url: String by property(url)
}

class ApiServerModel : ItemViewModel<ApiServer>() {
    val url = bind(ApiServer::url)

    override fun onCommit() {
        super.onCommit()

        //Save new API url to the client
        StationsApiClient.hostname = url.value
    }
}