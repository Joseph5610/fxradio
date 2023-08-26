package online.hudacek.fxradio.ui.view.stations

import javafx.beans.property.ListProperty
import javafx.geometry.Pos
import online.hudacek.fxradio.ui.BaseFragment
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.viewmodel.LibraryState
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.SearchViewModel
import tornadofx.action
import tornadofx.addClass
import tornadofx.bindChildren
import tornadofx.flowpane
import tornadofx.hyperlink
import tornadofx.paddingAll

/**
 * Shows provided tags list as FlowPane
 */
class TagsFragment : BaseFragment() {

    private val tagsProperty: ListProperty<String> by param()

    private val libraryViewModel: LibraryViewModel by inject()
    private val searchViewModel: SearchViewModel by inject()

    override val root = flowpane {
        hgap = 5.0
        vgap = 5.0
        paddingAll = 5
        alignment = Pos.CENTER
        bindChildren(tagsProperty) {
            hyperlink(it) {
                action {
                    libraryViewModel.stateProperty.value = LibraryState.Search(it, isTagSearch = true)
                    searchViewModel.bindQueryProperty.value = it
                    searchViewModel.searchByTagProperty.value = true
                }
                addClass(Styles.tag)
                addClass(Styles.grayLabel)
            }
        }
    }
}
