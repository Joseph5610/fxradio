package online.hudacek.fxradio.api.model

data class AddStationBody(val name: String = "",
                          val url: String = "",
                          val homepage: String = "",
                          val favicon: String = "",
                          val countrycode: String = "",
                          val country: String = "",
                          val language: String = "",
                          val tags: String = "")

data class AddStationResponse(val ok: Boolean, val message: String, val uuid: String)