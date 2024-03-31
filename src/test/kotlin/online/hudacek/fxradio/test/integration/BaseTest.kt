package online.hudacek.fxradio.test.integration

import javafx.application.Platform
import javafx.stage.Stage
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.Log
import online.hudacek.fxradio.viewmodel.LogViewModel
import online.hudacek.fxradio.viewmodel.Preferences
import online.hudacek.fxradio.viewmodel.PreferencesViewModel
import org.apache.logging.log4j.Level
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop
import tornadofx.find

/**
 * to make tests work on macOS:
 * allow IntelliJ IDEA in System Settings > Privacy > Accessibility
 */
@ExtendWith(ApplicationExtension::class)
abstract class BaseTest {

    private lateinit var app: FxRadio

    protected val robot: FxRobot = FxRobot()

    @Start
    protected fun loadApp(stage: Stage) {
        app = FxRadio(stylesheet = Styles::class, isAppRunningInTest = true)
        app.start(stage)

        Platform.runLater {
            // Disable app logger to have only relevant logs
            val logViewModel = find<LogViewModel>()
            logViewModel.item = Log(Level.INFO)
            logViewModel.commit()
        }
    }

    @Stop
    protected fun stopApp() = app.stop()
}
