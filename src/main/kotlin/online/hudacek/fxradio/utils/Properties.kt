/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.utils

import mu.KotlinLogging
import tornadofx.Component

private val logger = KotlinLogging.logger {}

/**
 * Keys for values stored in app.properties
 */
enum class Properties(val key: String) {
    UseNativeMenuBar("menu.native"),
    Volume("player.volume"),
    Player("player.type"),
    PlayerAnimated("player.animate"),
    PlayerRefreshMetaData("player.refreshMeta"),
    ApiServer("app.server"),
    SearchQuery("search.query"),
    SendOsNotifications("notifications"),
    WindowDivider("windowDivider"),
    ShowLibrary("window.showLibrary"),
    ShowCountries("window.showCountries"),
    ShowPinnedCountries("window.showPinned"),
    WindowWidth("window.width"),
    WindowHeight("window.height"),
    WindowX("window.x"),
    WindowY("window.y"),
    LogLevel("log.level");
}

/**
 * Creates app configuration for property key [Properties]
 */
class Property(property: Properties) : Component() {

    //Extract value
    val key = property.key

    val isPresent: Boolean
        get() = runCatching { app.config.keys.any { it == key } }.getOrDefault(false)

    inline fun <reified T> get(): T {
        return when (T::class) {
            Boolean::class -> app.config.boolean(key) as T
            Double::class -> app.config.double(key) as T
            Int::class -> app.config.int(key) as T
            String::class -> app.config.string(key) as T
            else -> {
                throw IllegalArgumentException("${T::class} is not supported argument")
            }
        }
    }

    /**
     * Return the value from the app.config based on key
     * or default value if the value does not exist there
     */
    inline fun <reified T> get(defaultValue: T): T {
        return when (T::class) {
            Boolean::class -> (app.config.boolean(key, defaultValue as Boolean)) as T
            Double::class -> app.config.double(key, defaultValue as Double) as T
            Int::class -> app.config.int(key, defaultValue as Int) as T
            String::class -> app.config.string(key, defaultValue as String) as T
            else -> {
                defaultValue
            }
        }
    }

    fun <T> save(newValue: T) {
        logger.debug { "Saving $key: $newValue " }
        with(app.config) {
            set(key to newValue)
            save()
        }
    }

    fun remove() {
        logger.debug { "Remove value for key: $key " }
        with(app.config) {
            remove(key)
            save()
        }
    }
}

fun Component.saveProperties(keyValueMap: Map<Properties, Any>) {
    logger.debug { "Saving ${keyValueMap.keys}, ${keyValueMap.values} " }
    with(app.config) {
        keyValueMap.forEach {
            set(it.key.key to it.value)
        }
        save()
    }
}

/**
 * Helper method. Get value of property. If the value is not stored, returns [defaultValue]
 */
inline fun <reified T> Properties.value(defaultValue: T) = Property(this).get(defaultValue)