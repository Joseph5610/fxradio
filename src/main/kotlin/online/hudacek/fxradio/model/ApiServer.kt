package online.hudacek.fxradio.model

import javafx.beans.property.StringProperty
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.StationsApi
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