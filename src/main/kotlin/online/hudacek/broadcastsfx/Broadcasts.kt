package online.hudacek.broadcastsfx

import mu.KotlinLogging
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.MainView
import tornadofx.App
import tornadofx.launch
import java.nio.file.Path
import java.nio.file.Paths

class Broadcasts : App(MainView::class, Styles::class) {
    private val logger = KotlinLogging.logger {}

    private val userHome: String = System.getProperty("user.home")
    override val configBasePath: Path = Paths.get(userHome + "/" + About.appName.toLowerCase() + "/conf")
}

fun main(args: Array<String>) {
    launch<Broadcasts>(args)
}