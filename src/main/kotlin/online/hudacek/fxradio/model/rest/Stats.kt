package online.hudacek.fxradio.model.rest

data class Stats(
        val supported_version: Int,
        val software_version: String,
        val status: String,
        val stations: Int,
        val stations_broken: Int,
        val tags: Int,
        val clicks_last_hour: Int,
        val clicks_last_day: Int,
        val languages: Int,
        val countries: Int
)