package online.hudacek.broadcastsfx.model

import online.hudacek.broadcastsfx.events.LibraryType
import org.controlsfx.glyphfont.FontAwesome

data class Library(val name: String = "", val type: LibraryType, val graphic: FontAwesome.Glyph)