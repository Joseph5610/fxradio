package online.hudacek.fxradio.model.rest

data class AddStationBody(val name: String,
                          val URL: String,
                          val homepage: String,
                          val favicon: String,
                          val country: String,
                          val countryCode: String,
                          val state: String,
                          val language: String,
                          val tags: String)