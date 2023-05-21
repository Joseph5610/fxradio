package online.hudacek.fxradio.test.integration

import javafx.stage.Stage
import online.hudacek.fxradio.test.elements.Player
import online.hudacek.fxradio.test.elements.StationsDataGrid
import online.hudacek.fxradio.test.util.waitFor
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import org.junit.jupiter.api.*
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop
import tornadofx.find

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("Player tests")
class PlayerTest : BaseTest() {

    @Start
    fun start(stage: Stage) = loadApp(stage)

    @Stop
    fun stop() = stopApp()

    @Test
    @Order(1)
    fun `App should play and pause selected station`() {
        // Verify app initial state
        val playerElement = Player(robot)
        playerElement.waitForStatusHasText("Streaming stopped")

        val clickedStationName = StationsDataGrid(robot)
            // Wait for stations to load
            .waitForElement()

            // Click on random station
            .clickOnRandomStation()

        // Wait for stream start
        val playerViewModel = find<PlayerViewModel>()
        waitFor(5) { playerViewModel.stateProperty.value is PlayerState.Playing }

        // Check that player has text with name of the station
        playerElement.waitForStatusHasText(clickedStationName)

        // Stop the stream
        playerElement.stopStream()

        // Wait until stream is stopped
        waitFor(2) { playerViewModel.stateProperty.value == PlayerState.Stopped }

        playerElement.waitForStatusHasText("Streaming stopped")
    }

    @Test
    fun `Click on volume icons should change slider value`() {
        val sliderMinValueExpected = -35.0
        val sliderMaxValueExpected = 1.0

        // Verify app initial state
        Player(robot)
            .waitForStatusHasText("Streaming stopped")

            // Verify volume icons near slider are visible
            .verifyVolumeIconsVisible()

            // Verify volume icon click changes the slider value
            .clickVolumeMinIcon()
            .verifySliderValueChanged(sliderMinValueExpected)

            // Verify volume icon click changes the slider value
            .clickVolumeMaxIcon()
            .verifySliderValueChanged(sliderMaxValueExpected)
    }
}
