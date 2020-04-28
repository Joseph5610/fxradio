package online.hudacek.broadcastsfx.model

import online.hudacek.broadcastsfx.events.LibraryType

data class Library(val name: String = "", val type: LibraryType) {
    override fun toString(): String {
        return name
    }
}