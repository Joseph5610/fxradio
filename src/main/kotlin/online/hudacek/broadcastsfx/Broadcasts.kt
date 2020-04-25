package online.hudacek.broadcastsfx

import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.MainView
import tornadofx.App
import tornadofx.launch
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Main class for the app
 */
class Broadcasts : App(MainView::class, Styles::class) {
    //override app.config path to user.home/fxradio
    override val configBasePath: Path = Paths.get(About.appConfigLocation)
}

fun main(args: Array<String>) = launch<Broadcasts>(args)
