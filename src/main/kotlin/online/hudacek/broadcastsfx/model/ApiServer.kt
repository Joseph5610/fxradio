package online.hudacek.broadcastsfx.model

import javafx.beans.property.StringProperty
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.StationsApi
import tornadofx.*

class ApiServer(url: String) {
    var url: String by property(url)
}

class ApiServerModel : ItemViewModel<ApiServer>() {
    val url = bind(ApiServer::url) as StringProperty

    override fun onCommit() {
        super.onCommit()

        //Save new API url to the client
        StationsApi.hostname = url.value

        //Save API server
        with(app.config) {
            set(Config.Keys.apiServer to url.value)
            save()
        }
    }
}