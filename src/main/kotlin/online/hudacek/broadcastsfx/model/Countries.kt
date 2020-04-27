package online.hudacek.broadcastsfx.model

data class Countries(
        val name: String,
        val stationcount: Int
) {
    override fun toString(): String {
        return "$name (${stationcount})"
    }
}