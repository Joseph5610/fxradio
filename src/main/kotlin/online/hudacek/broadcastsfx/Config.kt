package online.hudacek.broadcastsfx

object Config {

    val appConfigPath = System.getProperty("user.home") + "/" + About.appName.toLowerCase() + "/conf"
    val imageCachePath = System.getProperty("user.home") + "/" + About.appName.toLowerCase() + "/cache"

    object Keys {
        const val useNativeMenuBar = "menu.native"
        const val volume = "player.volume"
        const val playerType = "player.type"
        const val apiServer = "app.server"
        const val searchQuery = "search.query"
        const val playerAnimate = "player.animate"
    }

    object Flags {
        const val addStationEnabled = false
    }
}