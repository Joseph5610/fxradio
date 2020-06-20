package online.hudacek.fxradio.model

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.events.PlayerType
import online.hudacek.fxradio.model.rest.Station
import tornadofx.ItemViewModel
import tornadofx.onChange
import tornadofx.property

class Player(animate: Boolean = true, station: Station = Station.stub(),
             playerType: PlayerType, notifications: Boolean = true, volume: Double) {

    var animate: Boolean by property(animate)
    var notifications: Boolean by property(notifications)
    var station: Station by property(station)
    var playerType: PlayerType by property(playerType)
    var volume: Double by property(volume)
}

class PlayerModel : ItemViewModel<Player>() {

    private val stationsHistory: StationsHistoryModel by inject()

    val animate = bind(Player::animate) as BooleanProperty
    val notifications = bind(Player::notifications) as BooleanProperty
    val stationProperty = bind(Player::station) as ObjectProperty
    val playerType = bind(Player::playerType) as ObjectProperty
    val volumeProperty = bind(Player::volume) as DoubleProperty

    init {
        stationProperty.onChange {
            it?.let(stationsHistory::add)
        }
    }

    override fun onCommit() {
        //Save API server
        with(app.config) {
            set(Config.Keys.playerAnimate to animate.value)
            set(Config.Keys.playerType to playerType.value)
            set(Config.Keys.notifications to notifications.value)
            set(Config.Keys.volume to volumeProperty.value)
            save()
        }
    }
}

