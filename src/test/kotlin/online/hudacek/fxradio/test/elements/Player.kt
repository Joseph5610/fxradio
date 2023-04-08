package online.hudacek.fxradio.test.elements

import javafx.scene.control.Slider
import online.hudacek.fxradio.test.util.find
import online.hudacek.fxradio.test.util.hasLabel
import online.hudacek.fxradio.test.util.visible
import online.hudacek.fxradio.test.util.waitFor
import org.controlsfx.glyphfont.Glyph
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot

class Player(private val robot: FxRobot) {

    private val nowPlayingLabel = "#nowStreaming"
    private val volumeMinIcon = "#volumeMinIcon"
    private val volumeMaxIcon = "#volumeMaxIcon"
    private val volumeSlider = "#volumeSlider"
    private val playerControls = "#playerControls"

    fun waitForStatusHasText(text: String) = apply {
        verifyThat(nowPlayingLabel, hasLabel(text))
    }

    fun stopStream() = apply {
        val stopButton = robot.find(playerControls) as Glyph
        robot.clickOn(stopButton)
    }

    fun verifyVolumeIconsVisible() = apply {
        verifyThat(volumeMinIcon, visible())
        verifyThat(volumeMaxIcon, visible())
    }

    fun clickVolumeMinIcon() = apply {
        robot.clickOn(volumeMinIcon)
    }

    fun clickVolumeMaxIcon() = apply {
        robot.clickOn(volumeMaxIcon)
    }

    fun verifySliderValueChanged(expected: Double) = apply {
        val slider = robot.find(volumeSlider) as Slider
        waitFor(2) { slider.value == expected }
    }
}
