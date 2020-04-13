package online.hudacek.broadcastsfx

import com.apple.eawt.Application
import online.hudacek.broadcastsfx.views.MainView
import tornadofx.App
import tornadofx.launch
import java.awt.Image
import java.net.URL
import javax.swing.ImageIcon

class Main : App(MainView::class)

fun main(args: Array<String>) {
    try {
        val iconURL: URL = Main::class.java.getResource("Election-News-Broadcast-icon.png")
        val image: Image = ImageIcon(iconURL).image
        Application.getApplication().dockIconImage = image
    } catch (e: Exception) { // Won't work on Windows or Linux.
    }
    launch<Main>(args)
}