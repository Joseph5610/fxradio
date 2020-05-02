package online.hudacek.broadcastsfx.model

import online.hudacek.broadcastsfx.Config
import tornadofx.ItemViewModel
import tornadofx.property

class Player(animate: Boolean = true) {
    var animate: Boolean by property(animate)
}

class PlayerModel : ItemViewModel<Player>() {
    val animate = bind(Player::animate)

    override fun onCommit() {
        //Save API server
        with(app.config) {
            set(Config.playerAnimate to animate.value)
            save()
        }
    }
}

