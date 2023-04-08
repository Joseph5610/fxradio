package online.hudacek.fxradio.test.integration

import javafx.stage.Stage
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.Log
import online.hudacek.fxradio.viewmodel.LogViewModel
import online.hudacek.fxradio.viewmodel.PreferencesViewModel
import org.apache.logging.log4j.Level
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import tornadofx.find

@ExtendWith(ApplicationExtension::class)
abstract class BaseTest {

    private lateinit var app: FxRadio

    protected val robot: FxRobot = FxRobot()

    protected fun loadApp(stage: Stage) {
        app = FxRadio(stylesheet = Styles::class, isAppRunningInTest = true)
        app.start(stage)

        // Disable app logger to have only relevant logs
        val logViewModel = find<LogViewModel>()
        logViewModel.item = Log(Level.INFO)
        logViewModel.commit()

        val preferencesViewModel = find<PreferencesViewModel>()
        preferencesViewModel.useTrayIconProperty.value = false
    }

    protected fun stopApp() = app.stop()
}
