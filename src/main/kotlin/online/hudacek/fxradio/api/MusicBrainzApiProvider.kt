package online.hudacek.fxradio.api

import mu.KotlinLogging
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.apiclient.ServiceProvider
import online.hudacek.fxradio.apiclient.musicbrainz.MusicBrainzApi


private val logger = KotlinLogging.logger {}

object MusicBrainzApiProvider : AbstractApiProvider<MusicBrainzApi>() {

    override val serviceProvider by lazy {
        ServiceProvider(Config.API.musicBrainzApi).also {
            logger.info { "Initialized MusicBrainz API provider" }
        }
    }
}