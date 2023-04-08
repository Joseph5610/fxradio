package online.hudacek.fxradio.test.elements

import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.test.util.find
import online.hudacek.fxradio.test.util.waitFor
import online.hudacek.fxradio.viewmodel.SelectedStationViewModel
import org.testfx.api.FxRobot
import tornadofx.DataGrid
import tornadofx.find

class StationsDataGrid(private val robot: FxRobot) {

    private val stationsDataGrid = "#stations"

    fun waitForElement() = apply {
        val stations = getDataGridElement()
        waitFor(5) {
            stations.isVisible && stations.items.size > 0
        }
    }

    fun clickOnRandomStation(): String {
        val stations = getDataGridElement()
        val selectedStationViewModel = find<SelectedStationViewModel>()

        // Avoid station names that start with # as it is query locator for ID
        val stationToClick = stations.items
            .filter { !it.name.startsWith("#") }
            .filter { it.name != selectedStationViewModel.stationProperty.value.name }
            .take(5)
            .random()

        robot.interact {
            robot.doubleClickOn(stationToClick.name)
        }

        return stationToClick.name
    }

    fun getDataGridElement() = robot.find(stationsDataGrid) as DataGrid<Station>
}
