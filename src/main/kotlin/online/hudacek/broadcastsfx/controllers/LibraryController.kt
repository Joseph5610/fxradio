package online.hudacek.broadcastsfx.controllers

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.events.LibrarySearchChanged
import online.hudacek.broadcastsfx.events.LibraryRefreshEvent
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.model.rest.CountriesBody
import online.hudacek.broadcastsfx.model.Library
import online.hudacek.broadcastsfx.views.LibraryView
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.Controller
import tornadofx.observableListOf

class LibraryController : Controller() {

    private val libraryView: LibraryView by inject()

    private val stationsApi: StationsApi
        get() {
            return StationsApi.client
        }

    val libraryItems by lazy {
        observableListOf(
                Library(LibraryType.TopStations, FontAwesome.Glyph.THUMBS_UP),
                Library(LibraryType.Favourites, FontAwesome.Glyph.STAR),
                Library(LibraryType.History, FontAwesome.Glyph.HISTORY)
        )
    }

    init {
        getCountries()
    }

    fun searchStation(searchString: String) = fire(LibrarySearchChanged(searchString))

    fun getCountries(): Disposable = stationsApi
            .getCountries(CountriesBody())
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe(
                    {
                        libraryView.showCountries(it)
                    },
                    {
                        libraryView.showError()
                    }
            )

    fun loadStationsByCountry(country: String) = fire(LibraryRefreshEvent(LibraryType.Country, country))

    fun loadLibrary(libraryType: LibraryType) = fire(LibraryRefreshEvent(libraryType))
}