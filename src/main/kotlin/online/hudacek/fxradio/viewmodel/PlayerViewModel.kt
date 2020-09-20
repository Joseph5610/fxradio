package online.hudacek.fxradio.viewmodel

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.media.PlayerType
import tornadofx.ItemViewModel
import tornadofx.onChange
import tornadofx.property

class PlayerModel(animate: Boolean = true, station: Station = Station.stub(),
                  playerType: PlayerType, notifications: Boolean = true, volume: Double) {

    var animate: Boolean by property(animate)
    var notifications: Boolean by property(notifications)
    var station: Station by property(station)
    var playerType: PlayerType by property(playerType)
    var volume: Double by property(volume)
}

class PlayerViewModel : ItemViewModel<PlayerModel>() {

    private val stationsHistoryView: StationsHistoryViewModel by inject()

    val animateProperty = bind(PlayerModel::animate) as BooleanProperty
    val notificationsProperty = bind(PlayerModel::notifications) as BooleanProperty
    val stationProperty = bind(PlayerModel::station) as ObjectProperty
    val playerTypeProperty = bind(PlayerModel::playerType) as ObjectProperty
    val volumeProperty = bind(PlayerModel::volume) as DoubleProperty

    init {
        stationProperty.onChange {
            it?.let(stationsHistoryView::add)
        }
    }

    override fun onCommit() {
        //Save API server
        with(app.config) {
            set(Config.Keys.playerAnimate to animateProperty.value)
            set(Config.Keys.playerType to playerTypeProperty.value)
            set(Config.Keys.notifications to notificationsProperty.value)
            set(Config.Keys.volume to volumeProperty.value)
            save()
        }
    }
}

