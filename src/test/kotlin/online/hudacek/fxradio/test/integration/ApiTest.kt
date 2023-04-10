package online.hudacek.fxradio.test.integration

import javafx.stage.Stage
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.ServiceProvider
import online.hudacek.fxradio.apiclient.radiobrowser.RadioBrowserApi
import online.hudacek.fxradio.apiclient.radiobrowser.model.isIgnoredStation
import online.hudacek.fxradio.test.elements.StationsDataGrid
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop

@DisplayName("API tests")
class ApiTest : BaseTest() {

    private val service = ServiceProvider("https://${Config.API.fallbackApiServerURL}").create<RadioBrowserApi>()

    @Start
    fun start(stage: Stage) = loadApp(stage)

    @Stop
    fun stop() = stopApp()

    @Test
    fun `App should show the same top voted stations as received via API`() {
        // Wait for stations to load
        val stationsDataGrid = StationsDataGrid(robot)
            .waitForElement()

        // Get results from API
        val apiStations = service.getTopVotedStations().blockingGet()
            .filter { !it.isIgnoredStation }

        // Verify API and app results are the same
        stationsDataGrid.verifyNumberOfItems(apiStations.size)
    }
}
