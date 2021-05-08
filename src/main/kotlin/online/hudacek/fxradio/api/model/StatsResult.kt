package online.hudacek.fxradio.api.model

data class StatsResult(val supported_version: String,
                       val software_version: String,
                       val status: String,
                       val stations: String,
                       val stations_broken: String,
                       val tags: String,
                       val clicks_last_hour: Int,
                       val clicks_last_day: Int,
                       val languages: Int,
                       val countries: Int
)