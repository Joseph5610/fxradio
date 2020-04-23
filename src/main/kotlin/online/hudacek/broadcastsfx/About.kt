package online.hudacek.broadcastsfx

object About {
    const val appName = "FXRadio"
    const val appDesc = "Internet radio directory"
    const val appIcon = "Industry-Radio-Tower-icon.png"
    const val appVersion = "0.0.1"
    const val author = "Jozef Hudáček"
    const val copyright = "Copyright (c) 2020"
    const val dataSource = "https://api.radio-browser.info"

    val appConfigLocation = System.getProperty("user.home") + "/" + appName.toLowerCase() + "/conf"
    val imageCacheLocation = System.getProperty("user.home") + "/" + appName.toLowerCase() + "/cache"
}