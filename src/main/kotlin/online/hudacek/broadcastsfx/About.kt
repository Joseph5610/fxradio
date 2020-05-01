package online.hudacek.broadcastsfx

/**
 * Basic information about the app
 * mostly for displaying in About dialog
 */
object About {
    const val appName = "FXRadio"
    const val appDesc = "Internet radio directory"
    const val appLogo = "Election-News-Broadcast-icon.png"
    const val appIcon = "Industry-Radio-Tower-icon.png"
    const val author = "Jozef Hudáček"
    const val copyright = "Copyright (c) 2020"
    const val dataSource = "https://api.radio-browser.info"

    val appConfigLocation = System.getProperty("user.home") + "/" + appName.toLowerCase() + "/conf"
    val imageCacheLocation = System.getProperty("user.home") + "/" + appName.toLowerCase() + "/cache"
}