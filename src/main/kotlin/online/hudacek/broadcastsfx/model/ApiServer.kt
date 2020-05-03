package online.hudacek.broadcastsfx.model

import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.StationsApi
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
        StationsApi.hostname = url.value

        //Save API server
        with(app.config) {
            set(Config.apiServer to url.value)
            save()
        }
    }
}