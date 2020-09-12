package online.hudacek.fxradio.api.model

//Response
data class Countries(val name: String, val stationcount: Int)

//GET params
data class CountriesBody(val hidebroken: Boolean = true)