package online.hudacek.fxradio.api.model

data class AddStationBody(val name: String = "",
                          val url: String = "",
                          val homepage: String = "",
                          val favicon: String = "",
                          val country: String = "",
                          val countryCode: String = "",
                          val state: String = "",
                          val language: String = "",
                          val tags: String = "")

data class AddStationResponse(val ok: String, val message: String, val uuid: String)