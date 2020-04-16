package online.hudacek.broadcastsfx

import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.views.MainView
import tornadofx.App
import tornadofx.launch

class Main : App(MainView::class, Styles::class)

fun main(args: Array<String>) = launch<Main>(args)