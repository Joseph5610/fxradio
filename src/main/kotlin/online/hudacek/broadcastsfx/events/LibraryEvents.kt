package online.hudacek.broadcastsfx.events

import tornadofx.FXEvent

enum class LibraryType {
    Favourites, History, Country, TopStations
}

class LibrarySearchChanged(val searchString: String) : FXEvent()

class LibraryRefreshEvent(val type: LibraryType, val params: String? = null) : FXEvent()