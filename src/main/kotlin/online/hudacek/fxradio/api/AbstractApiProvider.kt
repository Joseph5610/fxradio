package online.hudacek.fxradio.api

import online.hudacek.fxradio.apiclient.ApiDefinition
import online.hudacek.fxradio.apiclient.ServiceProvider
import tornadofx.Component

abstract class AbstractApiProvider<T : ApiDefinition> : Component() {

    abstract val serviceProvider: ServiceProvider

    /**
     * Creates the service instance
     */
    inline fun <reified T : ApiDefinition> provide() = serviceProvider.create<T>()

    fun close() = serviceProvider.close()
}