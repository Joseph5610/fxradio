package online.hudacek.broadcastsfx.fragments

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.model.StationViewModel
import online.hudacek.broadcastsfx.ui.createImage
import tornadofx.*
import tornadofx.controlsfx.rating
import tornadofx.controlsfx.statusbar


class StationInfoFragment : Fragment() {

    private val currentStation: StationViewModel by inject()

    override fun onBeforeShow() {
        currentStage?.opacity = 0.85
    }

    override val root = vbox {
        setPrefSize(300.0, 300.0)
        currentStation.station.value?.let {
            title = it.name

            val codecBitrateInfo = it.codec + " (" + it.bitrate + ")"

            val list = observableListOf(
                    it.tags,
                    codecBitrateInfo,
                    it.country,
                    it.language,
                    it.lastcheckoktime)

            vbox {
                paddingAll = 20.0
                alignment = Pos.CENTER

                imageview {
                    createImage(this, it)
                    effect = DropShadow(30.0, Color.LIGHTGRAY)
                    fitHeight = 100.0
                    fitHeight = 100.0
                    isPreserveRatio = true
                }

                /*
                rating(0, 5) {
                    paddingTop = 10.0
                    maxHeight = 15.0
                }*/
            }

            listview(list)
            statusbar {
                rightItems.add(
                        hyperlink(it.homepage) {
                            action {
                                val hostServices = HostServicesFactory.getInstance(app)
                                hostServices.showDocument(it.homepage)
                            }
                        }
                )
            }
        }
    }
}