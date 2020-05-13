package online.hudacek.broadcastsfx

import javafx.stage.Stage
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.MainView
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Main class for the app
 */
class Broadcasts : App(MainView::class, Styles::class) {
    //override app.config path to user.home/fxradio
    override val configBasePath: Path = Paths.get(Config.appConfigPath)

    override fun start(stage: Stage) {
        with(stage) {
            minWidth = 600.0
            minHeight = 400.0
            super.start(this)
        }
    }

    companion object {
        fun getVersion(): String {
            return Broadcasts::class.java.getPackage().implementationVersion ?: "DEVELOPMENT"
        }
    }
}

fun main(args: Array<String>) = launch<Broadcasts>(args)
