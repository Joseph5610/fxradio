package online.hudacek.broadcastsfx

import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.MainView
import tornadofx.App
import tornadofx.launch
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Broadcasts : App(MainView::class, Styles::class) {
    override val configBasePath: Path = Paths.get(About.appConfigLocation)

    private val cacheBasePath: Path = Paths.get(About.imageCacheLocation)

    init {
        //prepare cache directory
        if (!Files.isDirectory(cacheBasePath)) {
            Files.createDirectories(cacheBasePath)
        }
    }
}

fun main(args: Array<String>) {
    launch<Broadcasts>(args)
}