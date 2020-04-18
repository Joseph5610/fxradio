package online.hudacek.broadcastsfx

import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.MainView
import tornadofx.App
import tornadofx.launch
import java.nio.file.Path
import java.nio.file.Paths

class Main : App(MainView::class, Styles::class) {
    private val userHome: String = System.getProperty("user.home")
    override val configBasePath: Path = Paths.get(userHome + "/" + About.appName.toLowerCase() + "/conf")

    init {
        with(config) {
            set("use.saved.server" to true)
            save()
        }

        println(config.boolean("use.saved.server", false))
    }
}

fun main(args: Array<String>) = launch<Main>(args)