package online.hudacek.fxradio.api.model

//POST params
data class AddStationBody(val name: String = "",
                          val URL: String = "",
                          val homepage: String = "",
                          val favicon: String = "",
                          val country: String = "",
                          val countryCode: String = "",
                          val state: String = "",
                          val language: String = "",
                          val tags: String = "")

//Response
data class AddStationResult(val ok: String, val result: String, val uuid: String)