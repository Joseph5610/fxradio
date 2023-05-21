package online.hudacek.fxradio.api

import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.ServiceProvider
import online.hudacek.fxradio.apiclient.musicbrainz.MusicBrainzApi
import tornadofx.Component

private val logger = KotlinLogging.logger {}

object MusicBrainzApiProvider : Component() {

    private val serviceProvider: ServiceProvider by lazy {
        ServiceProvider(Config.API.musicBrainzApi).also {
            logger.info { "Initialized MusicBrainz Art API provider" }
        }
    }

    /**
     * Creates the [MusicBrainzApi] service instance
     */
    fun provide(): MusicBrainzApi = serviceProvider.create()

    fun close() = serviceProvider.close()
}